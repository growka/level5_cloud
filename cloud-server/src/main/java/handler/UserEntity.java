package handler;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter

public class UserEntity {

    private String id;
    private String username;
    private String folder;

}
