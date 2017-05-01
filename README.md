# Image Server
A very simple dynamic image server by java, support zoom operation.

## Requirements
The only requirement is Java 8.

## Build
```
mvn package
```

## Run
```
java -Dimage_server.storage_path=/path/to/original_images -Dimage_server.zoom_path=/path/to/zoomed_images image-server-x.y.z.jar
```

## Use it
Then, access [http://localhost:8080/upload](http://localhost:8080/upload) try to upload a image, it will give you a fileId.

You can use this fileId to access image.

Suppose your fileId is ```2017/05/01/6f7b3e2c-27c6-4e04-8c13-25ce4d415c50.jpg```

Try to access:
- [http://localhost:8080/2017/05/01/6f7b3e2c-27c6-4e04-8c13-25ce4d415c50.jpg](http://localhost:8080/2017/05/01/6f7b3e2c-27c6-4e04-8c13-25ce4d415c50.jpg)  *The original image*
- [http://localhost:8080/2017/05/01/6f7b3e2c-27c6-4e04-8c13-25ce4d415c50.jpg?m=1&w=400&h=300](http://localhost:8080/2017/05/01/6f7b3e2c-27c6-4e04-8c13-25ce4d415c50.jpg?m=1&w=400&h=300)  *New image with size 400x300*
- [http://localhost:8080/2017/05/01/6f7b3e2c-27c6-4e04-8c13-25ce4d415c50.jpg?m=1&w=400](http://localhost:8080/2017/05/01/6f7b3e2c-27c6-4e04-8c13-25ce4d415c50.jpg?m=1&w=400)  *New square image with size 400x400*