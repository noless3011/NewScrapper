package demo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// Định nghĩa một lớp đối tượng đơn giản với một thuộc tính
class MyObject {
    int value;

    public MyObject(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

public class demo {
    public static void main(String[] args) {
        // Tạo danh sách các đối tượng
        List<MyObject> list = new ArrayList<>();
        list.add(new MyObject(5));
        list.add(new MyObject(3));
        list.add(new MyObject(7));
        list.add(new MyObject(1));

        // Sắp xếp danh sách theo giá trị của thuộc tính 'value'
        Collections.sort(list, new Comparator<MyObject>() {
            @Override
            public int compare(MyObject o1, MyObject o2) {
                return Integer.compare(o1.getValue(), o2.getValue());
            }
        });

        // In ra danh sách đã sắp xếp
        for (MyObject obj : list) {
            System.out.println(obj.getValue());
        }
    }
}