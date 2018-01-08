package bobrovskaya.rect12.dreamdiary;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * Created by rect on 12/17/17.
 */

@AllArgsConstructor
public class Dream {
    private @Getter int id;
    private @Getter String name;
    private @Getter String date;
    private @Getter String description;

    private static int lastDreamtId = 0;

}
