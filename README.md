# OpenGLESRecoder

###glrecorder is a library on Android which can record what you draw using opengl es. all you need to do are as follows

- using GLRecoder.init() in onSurfaceChanged
```java
init(int width, int height, EGLConfig eglConfig)
```
- start or stop record at appropriate time
```java
GLRecoder.startEncoder(new File("/sdcard/record.mp4"));

GLRecoder.stopEncoder();
```
- add GLRecoder.beginDraw() before you draw a frame and GLRecoder.endDraw() after finish draw a frame, which may be look like this

```java
GLRecoder.beginDraw();

drawSomething();

GLRecoder.endDraw();
```
- do not forget add permission
```
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```