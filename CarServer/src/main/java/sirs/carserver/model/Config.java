package sirs.carserver.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;


@Getter
@Setter
public class Config implements Serializable {
    private Long id;

    //ac
    private int out1;
    private int out2;

    //seat
    private int pos1;
    private int pos3;

    public Config() {
        this.out1 = 0;
        this.out2 = 0;
        this.pos1 = 0;
        this.pos3 = 6;
    }
}
