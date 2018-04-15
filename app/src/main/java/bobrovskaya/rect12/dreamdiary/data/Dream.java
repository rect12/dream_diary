package bobrovskaya.rect12.dreamdiary.data;

import java.util.List;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
public class Dream {
    private @Getter @Setter int id;
    private @Getter @Setter String name;
    private @Getter @Setter String date;
    private @Getter @Setter String description;
    private @Getter @Setter List<String> audioPaths;

}
