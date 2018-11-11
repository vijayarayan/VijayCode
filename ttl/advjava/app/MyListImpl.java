package ttl.advjava.app;

import java.util.ArrayList;
import java.util.List;

public class MyListImpl implements MyList, OtherInterface{

    @Override
    public void add(String s) {

    }

    @Override
    public void forEach() {
        MyList.super.forEach();

        OtherInterface.super.forEach();
    }
}
