package drinkwater.examples.multiservice.impl;

import drinkwater.examples.multiservice.IServiceC;

/**
 * Created by A406775 on 2/01/2017.
 */
public class ServiceCImpl implements IServiceC {

    private String connection;

    @Override
    public String findData(String searchInfo) {
        return String.format("C -> [%s : %s]", connection, searchInfo);
    }
}
