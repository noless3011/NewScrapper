package model;

import java.util.Arrays;
import java.util.List;

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
	private static final List<String> IMAGE_EXTENSIONS = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".bmp");

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
        for (String extension : IMAGE_EXTENSIONS) {
            if (url.toLowerCase().endsWith(extension)) {
                return true;
            }
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
