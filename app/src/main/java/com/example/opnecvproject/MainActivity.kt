package com.example.opnecvproject

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.example.opnecvproject.ml.AgeModel14
import com.example.opnecvproject.ml.GenderModel14
import com.example.opnecvproject.ml.Happysadmodel
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder


class MainActivity : ComponentActivity() {

    private lateinit var buttonChooseImage: Button
    private lateinit var buttonPredict: Button
    private lateinit var buttonTakePhoto: ImageButton
    private lateinit var imageView: ImageView
    private lateinit var imageCrop: ImageView
    private lateinit var textViewResults: TextView
    private var selectedBitmap: Bitmap? = null
    private val requestImageCapture = 2

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonChooseImage = findViewById(R.id.button_choose_image)
        buttonPredict = findViewById(R.id.button_predict)
        buttonTakePhoto = findViewById(R.id.button_take_photo)
        imageView = findViewById(R.id.image_view)
        imageCrop = findViewById(R.id.image_crop)
        textViewResults = findViewById(R.id.text_view_results)

        // Click listener for selecting the image
        buttonChooseImage.setOnClickListener {
            selectImageFromGallery()
        }

        buttonTakePhoto.setOnClickListener {
            takePhoto()
        }

        // Click listener for predicting and cropping face
        buttonPredict.setOnClickListener {
            if (selectedBitmap != null) {
                detectAndCropFace(selectedBitmap!!)
            } else {
                Log.e("Prediction", "No image selected!")
                textViewResults.text = "Please select an image first."
            }
        }
    }

    // Function to select image from gallery
    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    private fun takePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, requestImageCapture)
        } else {
            Log.e("Camera", "Camera app not found!")
        }
    }

    // Handle selected image result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                1 -> { // From gallery
                    val selectedImageUri = data?.data
                    selectedImageUri?.let {
                        val inputStream = contentResolver.openInputStream(it)
                        selectedBitmap = BitmapFactory.decodeStream(inputStream)
                        imageView.setImageBitmap(selectedBitmap)
                    }
                }
                requestImageCapture -> { // From camera
                    val extras = data?.extras
                    val imageBitmap = extras?.get("data") as Bitmap
                    selectedBitmap = imageBitmap
                    imageView.setImageBitmap(selectedBitmap)
                }
            }
        }
    }

    // Function to detect and crop the face
    @SuppressLint("SetTextI18n")
    private fun detectAndCropFace(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)

        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .build()

        val detector = FaceDetection.getClient(options)

        detector.process(image)
            .addOnSuccessListener { faces ->
                if (faces.isNotEmpty()) {
                    val face = faces[0]  // Assuming the first detected face
                    val croppedFace = cropFace(bitmap, face.boundingBox)
                    imageCrop.setImageBitmap(croppedFace)  // Display the cropped face

                    // Predict age using the cropped face
                    val age = predictAge(croppedFace)
                    val gender = predictGender(croppedFace)
                    val emotion = predictEmotion(croppedFace)

                    textViewResults.text = "Predicted Age: $age, Predicted Gender: $gender, Predicted Emotion: $emotion"

                }
            }
            .addOnFailureListener { e ->
                Log.e("Face Detection", "Failed to detect face: ${e.message}")
            }
    }

    // Function to crop the face from the original bitmap using a bounding box
    private fun cropFace(bitmap: Bitmap, boundingBox: Rect): Bitmap {
        Log.d("Face Detection", "Bounding box: $boundingBox")
        return Bitmap.createBitmap(
            bitmap,
            boundingBox.left.coerceAtLeast(0),
            boundingBox.top.coerceAtLeast(0),
            boundingBox.width().coerceAtMost(bitmap.width - boundingBox.left),
            boundingBox.height().coerceAtMost(bitmap.height - boundingBox.top)
        )
    }

    // Function to preprocess the image for the TensorFlow Lite model
    private fun preprocessImage(bitmap: Bitmap): ByteBuffer? {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 128, 128, true)  // Size for age model

        val byteBuffer = ByteBuffer.allocateDirect(1 * 128 * 128 * 1 * 4)  // Size for 1 image, 128x128 pixels, 1 channel, Float32
        byteBuffer.order(ByteOrder.nativeOrder())

        // Convert each pixel to grayscale and normalize
        for (y in 0 until 128) {
            for (x in 0 until 128) {
                val pixel = resizedBitmap.getPixel(x, y)
                val gray = (0.299 * (pixel shr 16 and 0xFF) + 0.587 * (pixel shr 8 and 0xFF) + 0.114 * (pixel and 0xFF)) / 255.0f
                byteBuffer.putFloat(gray.toFloat())
            }
        }

        // Log buffer size and some content
        Log.d("Preprocess", "Buffer size: ${byteBuffer.capacity()}")
        return byteBuffer
    }

    // Function to predict age using the TensorFlow Lite model
    private fun predictAge(bitmap: Bitmap): Int {
        return try {
            val byteBuffer = preprocessImage(bitmap) ?: return -1  // Return -1 if preprocessing fails

            // Load the age model
            val ageModel = AgeModel14.newInstance(this)

            // Prepare input buffer
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 128, 128, 1), DataType.FLOAT32)
            inputFeature0.loadBuffer(byteBuffer)

            // Run inference
            val outputs = ageModel.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            // Get the predicted age
            val predictedAge = outputFeature0.floatArray[0].toInt()  // Assuming the output is a single float representing age

            // Release the model resources
            ageModel.close()

            predictedAge

        } catch (e: Exception) {
            Log.e("AgePrediction", "Error during prediction: ${e.message}")
            -1  // Return -1 in case of error
        }
    }

    // Function to predict gender using the TensorFlow Lite model
    private fun predictGender(bitmap: Bitmap): Pair<String, Float> {
        return try {
            val byteBuffer = preprocessImage(bitmap) ?: return Pair("Unknown", 0.0f)  // Return "Unknown" if preprocessing fails

            // Load the gender model
            val genderModel = GenderModel14.newInstance(this)

            // Prepare input buffer
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 128, 128, 1), DataType.FLOAT32)
            inputFeature0.loadBuffer(byteBuffer)

            // Run inference
            val outputs = genderModel.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            // Get the predicted gender probability (Assuming binary classification: 0 -> Male, 1 -> Female)
            val femaleProbability = outputFeature0.floatArray[0]

            // Colab logic: if female probability > 0.5, predict "Female", otherwise predict "Male"
            val (gender, probability) = if (femaleProbability > 0.5) {
                "Female" to femaleProbability
            } else {
                "Male" to 1 - femaleProbability
            }

            // Release the model resources
            genderModel.close()

            Pair(gender, probability)

        } catch (e: Exception) {
            Log.e("GenderPrediction", "Error during prediction: ${e.message}")
            Pair("Unknown", 0.0f)  // Return "Unknown" in case of error
        }
    }

    // Function to preprocess the image for the TensorFlow Lite model (similar to Colab)
    private fun preprocessEmotionImage(bitmap: Bitmap): ByteBuffer? {
        // Resize image to 48x48
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, 48, 48, true)

        // Prepare a ByteBuffer (1 image, 48x48, 3 channels, float32)
        val byteBuffer = ByteBuffer.allocateDirect(1 * 48 * 48 * 3 * 4)
        byteBuffer.order(ByteOrder.nativeOrder())

        // Convert image to normalized RGB values (0 to 1 range)
        for (y in 0 until 48) {
            for (x in 0 until 48) {
                val pixel = resizedBitmap.getPixel(x, y)

                // Extract RGB values
                val red = (pixel shr 16 and 0xFF).toFloat() / 255.0f
                val green = (pixel shr 8 and 0xFF).toFloat() / 255.0f
                val blue = (pixel and 0xFF).toFloat() / 255.0f

                // Put RGB values into ByteBuffer
                byteBuffer.putFloat(red)
                byteBuffer.putFloat(green)
                byteBuffer.putFloat(blue)
            }
        }

        return byteBuffer
    }


    // Function to predict emotion (mirroring Colab's logic)
    private fun predictEmotion(bitmap: Bitmap): Pair<String, Float> {
        return try {
            // Preprocess the image for the model
            val byteBuffer = preprocessEmotionImage(bitmap) ?: return Pair("Unknown", 0.0f)

            // Load the TensorFlow Lite model
            val emotionModel = Happysadmodel.newInstance(this)

            // Prepare input buffer for the RGB image
            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 48, 48, 3), DataType.FLOAT32)
            inputFeature0.loadBuffer(byteBuffer)

            // Run inference
            val outputs = emotionModel.process(inputFeature0)
            val outputFeature0 = outputs.outputFeature0AsTensorBuffer

            // Get raw output (assuming first element is 'Sad' probability)
            val sadProbability = outputFeature0.floatArray[0]

            // Predict emotion based on the probability
            val (emotion, probability) = if (sadProbability > 0.5) {
                "Sad" to sadProbability
            } else {
                "Happy" to 1 - sadProbability
            }

            // Release the model resources
            emotionModel.close()

            Pair(emotion, probability)

        } catch (e: Exception) {
            Log.e("EmotionPrediction", "Error during prediction: ${e.message}")
            Pair("Unknown", 0.0f)
        }
    }






}