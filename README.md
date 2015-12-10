# AffectButton
The AffectButton java code

See also www.joostbroekens.com

The AffectButton can be used by people to report their emotional state (feeling) in a simple one-click way. It can also be used for emotional tagging of media and other content. Recently the AffectButton, a standard interface component for affect (emotion) self report has been evaluated in a several large studies (800+ subjects of varied demographics). Our research shows that people can indeed use the button, and that the measurements are reliable and valid. You can try out the button yourself here, or download the stand alone Java version for Java, HTML5, Python and Android.
For usage instructions for users and researchers see instructions.


License of use
The AffectButton is distributed under the Creative Commons License CC-BY-NC-SA, unless otherwise agreed in writing. In short this means that you can use the button in your own research or educational project, that you may alter the button but not state that alterations are endorse by me, and that you must always credit the inventors. We prefer you credit us by citing our most recent work:

AffectButton: a method for reliable and valid affective self-report 
Broekens, J., & Brinkman, W.-P. (2013). International Journal of Human-Computer Studies, 71(6), 641-667.

For commercial use you can contact me at my email above.

Versions
The AffectButton in the applet and stand alone version is the latest validated version (v3.2) and is controlled with the mouse (or another pointing device). You simply hover over the button, the face changes, and when you feel the face represents your emotion, mood, or your opinion about a particular thing, you click. When using the AffectButton for your research, please always check and download the latest version. The current version is v3.2 (latest validated version and reported upon in the above mentioned publication) or v3.3 (latest experimental version solving the problem of "nonresposiveness of arousal in the low-arousal states). If you need better arousal measures at low levels of arousal, use v3.3. For more info see the paper cited above. 

JAVA
The Java code for v3.3 can be downloaded AffectButton v3.3, or here (v3.2): AffectButton v3.2. The code is in the jar file. The jar file itself is an executable one containing a test app. The test app can be used to record affect self-report for experiments. You can run the app (when you have Java installed) by double clicking the downloaded jar file. The application produces an output file in the directory in which you put the jar file. Each comma separated line in the output file contains the timestamp the app was started, the timestamp of the click, and the sequence number of the click, and of course the Pleasure, Arousal and Dominance values for that click. Alternatively you can run the jar with a command line argument. This argument will replace the first timestamp on each line, so that you can include e.g. a subject number.

PYTHON
There is a validated Python implementation (2.7.1) of the AffectButton v3.2 available now. It also contains an executable. To use the python version you need to:
1. Install Python 2.7.1
2. Install pyqt for python 2.7 (python qt library). Here's a download.
3. Download and run or use the affectbutton. You can download it here. 

HTML5 / j-query / mobile
There is a validated version in HTML5 for v3.3. no need to install Java or Python. It runs in standard HTML5 browsers, and on mobiles, and is j-query compatible. download the rar below. Therein you will find the standard version of the AffectButton embedded in a demo page. Now the button is configured to react as is common in mouse-based systems, so a click enters the values for P, A and D, while moving around changes the face, just like the Java and Python versions. You can configure it to react on dragging (for touch-based devices), but then you need a button to input the actual values (uncomment the HTML buttons in the HTML file). In addtion you need to change the event handler function by uncommenting the code in _doMouse() in jquery.ui.affectbutton.js Here's a download.