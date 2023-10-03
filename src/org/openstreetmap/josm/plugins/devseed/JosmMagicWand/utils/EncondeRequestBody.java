package org.openstreetmap.josm.plugins.devseed.JosmMagicWand.utils;

public class EncondeRequestBody {
    private String encoded_image;

    public EncondeRequestBody(String encodedImage) {
        encoded_image = encodedImage;
    }

    public String getEncoded_image() {
        return encoded_image;
    }

    public void setEncoded_image(String encoded_image) {
        this.encoded_image = encoded_image;
    }


}
