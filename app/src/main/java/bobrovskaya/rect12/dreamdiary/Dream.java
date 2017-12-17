package bobrovskaya.rect12.dreamdiary;

import java.util.ArrayList;

/**
 * Created by rect on 12/17/17.
 */

public class Dream {
    private String name;
    private String date;
    private String description;

    public Dream(String name, String date, String description) {
        this.date = date;
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    private static int lastDreamtId = 0;

}
