package sirs.motorist.cli.model;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
public class Config implements Serializable {
    //ac
    private int out1;
    private int out2;

    //seat
    private int pos1;
    private int pos3;

    public Config(int out1, int out2, int pos1, int pos3) {
        this.out1 = out1;
        this.out2 = out2;
        this.pos1 = pos1;
        this.pos3 = pos3;
    }
}
