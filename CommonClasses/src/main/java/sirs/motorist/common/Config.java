package sirs.motorist.common;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Config implements Serializable {

    //ac
    private int acOut1;
    private int acOut2;

    //seat
    private int acPos1;
    private int acPos3;

    public Config(int out1, int out2, int pos1, int pos3) {
        this.acOut1 = out1;
        this.acOut2 = out2;
        this.acPos1 = pos1;
        this.acPos3 = pos3;
    }

    public Config() {
        this.acOut1 = 0;
        this.acOut2 = 0;
        this.acPos1 = 0;
        this.acPos3 = 0;
    }

    @Override
    public String toString() {
        return "\n\tout1 = " + acOut1 + "\n" +
                "\tout2 = " + acOut2 + "\n" +
                "\tpos1 = " + acPos1 + "\n" +
                "\tpos3 = " + acPos3 + "\n";
    }
}
