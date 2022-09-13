# Java socket project
## Introduction 
The goal of the project is to create VSFlix, a peer to peer video/audio streaming application with a synchronization server 
### Applications description: 
The server and client applications should be coded in java based on the libraries discovered during the module lessons. Keep it simple. The system will stream audio from one client to another and the audio file that can be streamed are synchronized on the server. 

 
Figure1: Description of the system
## Work to do: 

__Minimal__ specifications of the __server__: 
* The server must be able to: 
  * Register new clients 
  * Maintain a list of registered clients (and the files they can stream) 
  * Give back a client IP and its list of audio file to another client
  * Accept multiple clients simultaneously (use threads). 
* The server must be able to write logs:
  * On a file 
  * The history must be kept (one file per month)
  * 3 levels of log (info, warning, severe) should be handled 
    * Info for all the useful operations
    * Warning for all the possible network errors
    * Severe for the exceptions
* You can use command words to discuss between the client and the server or use different ports on the server for the client communications 







__Minimal__ specifications of the __client__: 
* The client will be able to connect to the server through socket connections 
* The client should be able to give its list of file to the server 
* The client should be able to give its IP address 
* The client should be able to get a list of clients with their available audio files 
* The client should be able to ask for another client IP address 
* The client should be able to connect to another client and ask to stream one file 
* The client should be able to accept a network connection from another client and stream the selected file
* The client should be able to play the video/audio stream
* The client should NOT store temporary video/audio files 

The features described hereafter are examples of possible add-on that could be developed according to the progress of your project: 
* A client can handle stream video/audio files to multiple clients simultaneously 
* A client can handle video/audio files as well
* Your imagination (must be related to java socket)
## Tips: 
The client is quite complex and to simplify its development you can develop 2 client applications without threads first. Then when the code is working properly you can put the 2 codes together and use threads. 
## Deliverables: 
* 2 running applications in production state.
  * Commented code 
  * The 2 applications must behave properly without bugs 
  * __No useless code or libraries should remain__
  * All the features described under « work to do » must be implemented. 
    * If all the functionalities are developed properly for audio streaming with a 5. 
    * Adding new functionalities will increase the evaluation to reach a 6.
* Use Java with Eclipse, provide a .jar for both the client and the server. 
* Provide technical documentation of you work.
* Provide a user manual for the client application (short but clear, snapshots welcome). 
* 1 log book per person that will help us to understand your project sequence events, the difficulties you had, with a half day resolution. 

* How to prepare the presentation of your project 
  * Presentation of the project by the 2 students. During this presentation the professors should be able to clearly determine the contribution of each student. 
  * Demo first and then explain what you have done 
## Organisation 
### Details 
* 2 Java programs 
* Beware the copy paste! a check of the code against plagia will be performed 
* The code should be developed by 2 people at maximum. 

### Technical aspects 
* Implementation / Development 
  * Programming language : Java 
  * The programs must run with on windows 10 
### Tips 
* Start directly with an excellent code writing (comments, exceptions, naming, etc…) instead of trying to repair things at the end of the project. 
* Respect the development standards and the concepts seen in the lessons 
* We do prefer clean code and functions without bugs than buggy half backed solutions. The server must be coded to insure a 24/7 exploitation. 
### Organization: 

The presentation will be scheduled on __the last day of the course, the full project will be delivered at the same time.__

As a copy of your work we would like a zip file containing: 
* The complete source code with a description on how to recompile it on an Eclipse environment using Java
* The log book that will allow us to understand the tasks you’ve carried out, the problems you encountered, with a half day resolution.


