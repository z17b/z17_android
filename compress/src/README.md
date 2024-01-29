# Let's compress the image size!
#### Compress Image File
```java
compressedImageFile = new Compressor(this).compressToFile(actualImageFile);
```
#### Compress Image File to Bitmap
```java
compressedImageBitmap = new Compressor(this).compressToBitmap(actualImageFile);
```
### I want custom Compressor!
```java
compressedImage = new Compressor(this)
            .setMaxWidth(640)
            .setMaxHeight(480)
            .setQuality(75)
            .setCompressFormat(Bitmap.CompressFormat.WEBP)
            .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
              Environment.DIRECTORY_PICTURES).getAbsolutePath())
            .compressToFile(actualImage);
```
### Stay cool compress image asynchronously with RxJava!
```java
new Compressor(this)
        .compressToFileAsFlowable(actualImage)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<File>() {
            @Override
            public void accept(File file) {
                compressedImage = file;
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                throwable.printStackTrace();
                showError(throwable.getMessage());
            }
        });
```