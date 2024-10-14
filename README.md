# Age, Gender, and Emotion Recognition App

## Overview
This project presents a mobile application that utilizes machine learning models to predict a person's age, gender, and emotional expression (happy or sad) based on facial images. The application is designed for Android devices and leverages efficient edge computing to ensure real-time performance.

## Features
- Age and Gender Prediction: Determine a person's age and gender from facial images.
- Emotion Detection: Recognize and classify emotions as happy or sad.
- User-Friendly Interface: An intuitive design for easy navigation and interaction.

## Getting Started

## Requirements

- Android Studio (latest version recommended)
- Android SDK (API Level 21 or higher)
- TensorFlow Lite models (included in the repository)
- A device with a camera (optional, for photo capture functionality)
  
### Cloning the Repository
To get started with the project, clone the repository using the following command:

```bash
git clone https://github.com/Matvej911/Matvejs_edgeAi.git
```
### Opening in Android Studio
- Open Android Studio.
- Select Open an existing Android Studio project.
- Navigate to the cloned repository and select it.

### Model Files
The repository includes the following Jupyter notebooks:

- ```age_gender_models.ipynb```: Contains the implementation of age and gender prediction models.
- ```emotion_model.ipynb```: Contains the implementation of the emotion recognition model.
  
### Testing Images
The images_testing.zip file includes 20 images (50% adults and 50% elderly, with an equal distribution of male and female subjects, and a 50-50% split of happy and sad expressions).

### Datasets
- The emotion dataset used for training can be found in the emotion.zip file.
- The dataset for age and gender prediction can be downloaded from UTKFace Dataset on Kaggle ```https://www.kaggle.com/datasets/jangedoo/utkface-new``` (Note: This file is too large for GitHub, so download it separately).

## Usage
- Launch the app on an Android device.
- Select an image from the gallery or capture a new photo using the camera.
- The app will process the image and provide predictions for age, gender, and emotional expression.

## How to Use the App

### 1. Select an Image 
- You can easily select an image from your gallery to get predictions for age, gender, and emotion. Click the "Select Image" button, browse your gallery, and choose an image. After selecting, hit "Predict" to see the results.

![selectgif1](https://github.com/user-attachments/assets/fd410f1a-8ec2-4162-8bdd-32b6ec78da77)

### 2. Take a Photo
- Alternatively, you can use the app’s camera functionality to take a live photo. Tap the "Take Photo" button to open the camera, capture an image, and get instant predictions for age, gender, and emotion.
  
![fotogif](https://github.com/user-attachments/assets/c35688d2-ab8f-4a9e-a6b8-ab4d4f07f148)

### App Functionality
- Image Selection and Capture:
Users can either select an image from their device's gallery or take a new photo using the camera. This process is made easy through intuitive buttons on the user interface.

- Face Detection and Cropping:
The app uses Google ML Kit to detect faces within the selected or captured images. Once a face is detected, the app crops the image to focus on the face for optimal input.

- Real-time Predictions:
After detecting and cropping the face, the app predicts age, gender, and emotion using machine learning models. Each model processes the face image and outputs results, which are displayed on the screen.

- Model Integration:
TensorFlow Lite enables the app to efficiently run machine learning models on mobile devices. The age prediction model processes images resized to 128x128 pixels, while the emotion model uses 48x48 pixel images to maintain accuracy without overloading device resources.

### Model Performance
#### The app delivers real-time predictions for age, gender, and emotion with optimized accuracy. Below are the key performance metrics:
![Screenshot 2024-10-14 183605](https://github.com/user-attachments/assets/51572572-5e84-45b1-a8f3-b467ccc25771)
- These metrics show high performance, particularly on Edge AI devices, with perfect accuracy and balanced precision, recall, and F1-scores for predicting emotions.

#### The app also performs gender classification with the following results across both CPU and Edge AI devices:
![Screenshot 2024-10-14 183624](https://github.com/user-attachments/assets/ac8a02d5-1dbe-4a23-8866-5c6a9fdcd85b)
- These metrics indicate a significant improvement in gender classification accuracy and F1-scores when running on Edge AI, showing the app’s adaptability and performance across different devices.
  

#### The app also provides predictions for age, and on Edge AI, the Mean Absolute Error (MAE) for age prediction is 10.95. This demonstrates a reasonable level of accuracy when estimating the age of individuals from images.

### Contributing
Contributions are welcome! If you have suggestions for improvements or new features, please open an issue or submit a pull request.
