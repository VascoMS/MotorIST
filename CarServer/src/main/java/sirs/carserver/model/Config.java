package sirs.carserver.model;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Config {
    private Long id;

    //ac
    private int out1;
    private int out2;

    //seat
    private int pos1;
    private int pos3;
}
