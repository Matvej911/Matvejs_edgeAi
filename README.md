# Age, Gender, and Emotion Recognition App

## Overview
This project presents a mobile application that utilizes machine learning models to predict a person's age, gender, and emotional expression (happy or sad) based on facial images. The application is designed for Android devices and leverages efficient edge computing to ensure real-time performance.

## Features
- Age and Gender Prediction: Determine a person's age and gender from facial images.
- Emotion Detection: Recognize and classify emotions as happy or sad.
- User-Friendly Interface: An intuitive design for easy navigation and interaction.

## Getting Started

### Prerequisites
- Android Studio
- Kotlin support enabled

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


### Contributing
Contributions are welcome! If you have suggestions for improvements or new features, please open an issue or submit a pull request.
