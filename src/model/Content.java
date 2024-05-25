package model;

import java.util.ArrayList;
import java.util.List;

public class Content {
	private ArrayList<Object> elements;

	public Content() {
		elements = new ArrayList<Object>();
	}

	public Content(Object... initElements) {
		elements = new ArrayList<Object>();
		for (Object o : initElements) {
			if (CheckElementValidity(o)) {
				elements.add(o);
			}
		}
	}

	private static boolean CheckElementValidity(Object element) {
		if (element instanceof String || element instanceof Image) {
			return true;
		}
		return false;
	}

	public List<String> getParagraphList() {
		List<String> paragraphs = new ArrayList<>();
		for (Object element : elements) {
			if (element instanceof String) {

				paragraphs.add((String) element);
			}
		}
		return paragraphs;
	}

	public void AddElement(Object element) {
		if (CheckElementValidity(element)) {
			elements.add(element);
		}
	}

	public void AddElement(Object element, int index) {
		if (CheckElementValidity(element)) {
			elements.add(index, element);
		}
	}

	public List<String> getElements() {
		List<String> getelements = new ArrayList<>();
		for (Object element : elements) {
			getelements.add(element.toString());
		}
		return getelements;
	}

	public void RemoveElement(int index) {
		elements.remove(index);
	}

	public void UpdateElement(Object element, int index) {
		if (CheckElementValidity(element)) {
			elements.set(index, element);
		}
	}

	@Override
	public String toString() {
		String s = new String();
		for (Object element : elements) {
			s += element.toString();
		}
		return s;
	}

}
