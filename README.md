I did a six month internship on Deep Learning at IMPLab, an image processing, mobile vision and pattern recognition lab. I created a system based on Deep Convolutional Generative Adversarial Networks that allow to discover and edit continuos attributes in images in an unspervised way. 

In particular I've created a mobile app, with PyTorch mobile that allow the user to take a pic of himself or upload it from the storate and modifiy his attributes keeping the identity of the person. The app is able to recreate an new image from the input image with specific attributes changed.

Another feature that could be implement on mobile (already available in the desktop application) is the feature transfer from two images. (Future Release)

Here on GitHub you can find the complete mobile app code. Based on the upload limitation of GitHub, I could not upload directly here the GAN models so, here it is the direct link to download the models: https://drive.google.com/drive/folders/18-kAVd5zdmMFK1KCtuq1KI-O9IWm_eSz?usp=sharing

You just have to upload them in the asset folder in the application in order to let the app build and run!  

The real challenge of building this mobile application was to create some sort of lite GAN that could be able to run on mobile environment with, of course, lower resources.

In order to that, starting from the main GAN ( I will upload the Python code of the GAN in the future ), I had to implement a system called Teacher and Student, using the feature transfer paradigm in orther to take as much information as possible from the Teacher.

What I had to figure out was to have some kind of Teacher (The initial trained network that was alread performing good in a desktop application) helping the Student (The new lite version of the network) to learn the same feature and information as the Teacher.

Here some results of the 24 attributes learned from the network:


![Strisciate1](https://user-images.githubusercontent.com/104820500/167724962-d354cf93-b262-47bb-b875-a8fda3939804.png)
![Strisciate2](https://user-images.githubusercontent.com/104820500/167724972-e9de2416-2a08-487a-ba62-8e600a788629.png)


As we can see, in the image, for each line we have a specific attribute and some variations on that particular attribute on a scale from -1 to 1.
