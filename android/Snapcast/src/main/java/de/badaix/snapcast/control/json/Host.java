package de.badaix.snapcast.control.json;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by johannes on 06.01.16.
 */
public class Host implements JsonSerialisable {
    String name = "";
    String mac = "";
    String os = "";
    String arch = "";
    String ip = "";

    public Host(JSONObject json) {
        fromJson(json);
    }

    public Host() {

    }

    @Override
    public void fromJson(JSONObject json) {
        try {
            name = json.getString("name");
            mac = json.getString("mac");
            os = json.getString("os");
            arch = json.getString("arch");
            ip = json.getString("ip");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("mac", mac);
            json.put("os", os);
            json.put("arch", arch);
            json.put("ip", ip);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public String getName() {
        return name;
    }

    public String getMac() {
        return mac;
    }

    public String getOs() {
        return os;
    }

    public String getArch() {
        return arch;
    }

    public String getIp() {
        return ip;
    }

    @Override
    public String toString() {
        return "Client{" +
                "name='" + name + '\'' +
                ", mac='" + mac + '\'' +
                ", os='" + os + '\'' +
                ", arch='" + arch + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Host that = (Host) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (mac != null ? !mac.equals(that.mac) : that.mac != null) return false;
        if (os != null ? !os.equals(that.os) : that.os != null) return false;
        if (arch != null ? !arch.equals(that.arch) : that.arch != null) return false;
        return !(ip != null ? !ip.equals(that.ip) : that.ip != null);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (mac != null ? mac.hashCode() : 0);
        result = 31 * result + (os != null ? os.hashCode() : 0);
        result = 31 * result + (arch != null ? arch.hashCode() : 0);
        result = 31 * result + (ip != null ? ip.hashCode() : 0);
        return result;
    }
}
