package software.amazon.fms.notificationchannel;

import java.util.Map;
import org.json.JSONObject;
import org.json.JSONTokener;

class Configuration extends BaseConfiguration {

    public Configuration() {
        super("aws-fms-notificationchannel.json");
    }
}
