package model;

import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class Image {
	public Image(String url) {
		super();
		this.url = url;
		this.description = new String();	
	}

	public Image(String url, String description) {
		super();
		this.url = url;
		this.setDescription(description);
	}

	private String url;
	private String description;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) throws MyException{
		if(isImageUrl()) {
			this.url = url;
		}else {
			throw new MyException("This is not a image url");
		}
		
	}
	
    public boolean isImageUrl() {
    	
        try {
			if (ImageIO.read(new URL(url)) == null) {
			    return true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return false;
    }

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return this.url;
	}
}
