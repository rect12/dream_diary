package bobrovskaya.rect12.dreamdiary;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.Setter;

/**
 * Created by rect on 12/17/17.
 */

@AllArgsConstructor
public class Dream {
    private @Getter @Setter int id;
    private @Getter @Setter String name;
    private @Getter @Setter String date;
    private @Getter @Setter String description;

    private static int lastDreamtId = 0;

}
