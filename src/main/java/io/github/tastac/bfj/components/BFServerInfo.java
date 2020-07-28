package io.github.tastac.bfj.components;

import com.google.gson.*;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * <p>Information about the server that has been queried from the Battlefields API.</p>
 *
 * @author Ocelot
 */
public class BFServerInfo
{
    private final String ip;
    private final int port;
    private final String motd;
    private final int onlinePlayers;
    private final int maxPlayers;
    private final String[] onlinePlayerNames;
    private final String version;
    private final boolean online;
    private final int protocol;
    private final String hostname;
    private final String icon;

    private BFServerInfo(String ip, int port, String motd, int onlinePlayers, int maxPlayers, String[] onlinePlayerNames, String version, boolean online, int protocol, String hostname, @Nullable String icon)
    {
        this.ip = ip;
        this.port = port;
        this.motd = motd;
        this.onlinePlayers = onlinePlayers;
        this.maxPlayers = maxPlayers;
        this.onlinePlayerNames = onlinePlayerNames;
        this.version = version;
        this.online = online;
        this.protocol = protocol;
        this.hostname = hostname;
        this.icon = icon;
    }

    /**
     * @return The ip of the server
     */
    public String getIp()
    {
        return ip;
    }

    /**
     * @return The server port
     */
    public int getPort()
    {
        return port;
    }

    /**
     * @return The text that displays under the server name as a JSON text component
     */
    public String getMotd()
    {
        return motd;
    }

    /**
     * @return The current amount of players online
     */
    public int getOnlinePlayers()
    {
        return onlinePlayers;
    }

    /**
     * @return The maximum amount of players that can be online at any time
     */
    public int getMaxPlayers()
    {
        return maxPlayers;
    }

    /**
     * @return An array of the usernames of the players on the server
     */
    public String[] getOnlinePlayerNames()
    {
        return onlinePlayerNames;
    }

    /**
     * @return The server minecraft version
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * @return Whether or not online mode is enabled on the server
     */
    public boolean isOnline()
    {
        return online;
    }

    /**
     * @return The server protocol
     */
    public int getProtocol()
    {
        return protocol;
    }

    /**
     * @return The server host name
     */
    public String getHostname()
    {
        return hostname;
    }

    /**
     * @return The icon image data for the server
     */
    @Nullable
    public String getIcon()
    {
        return icon;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof BFServerInfo)) return false;
        BFServerInfo that = (BFServerInfo) o;
        return this.port == that.port && this.protocol == that.protocol && this.ip.equals(that.ip) && this.version.equals(that.version);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(this.ip, this.port, this.version, this.protocol);
    }

    @Override
    public String toString()
    {
        return "BFServerInfo{" +
                "ip=" + this.ip +
                ", port=" + this.port +
                ", motd='" + this.motd.replaceAll("\n", "\\n") + '\'' +
                ", onlinePlayers=" + this.onlinePlayers +
                ", maxPlayers=" + this.maxPlayers +
                ", onlinePlayerNames=" + Arrays.toString(this.onlinePlayerNames) +
                ", version='" + this.version + '\'' +
                ", online=" + this.online +
                ", protocol=" + this.protocol +
                ", hostname='" + this.hostname + '\'' +
                ", icon='" + this.icon + '\'' +
                '}';
    }

    public static class Deserializer implements JsonDeserializer<BFServerInfo>
    {
        @Override
        public BFServerInfo deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
        {
            JsonObject jsonObject = json.getAsJsonObject();
            String ip = jsonObject.get("ip").getAsString();
            int port = jsonObject.get("port").getAsInt();

            StringBuilder motd = new StringBuilder();
            jsonObject.get("motd").getAsJsonObject().get("raw").getAsJsonArray().forEach(jsonElement -> motd.append(jsonElement.getAsString()).append('\n'));

            JsonObject playersJson = jsonObject.get("players").getAsJsonObject();
            int onlinePlayers = playersJson.get("online").getAsInt();
            int maxPlayers = playersJson.get("max").getAsInt();
            List<String> onlinePlayerNames = new ArrayList<>();
            if (playersJson.has("list"))
                playersJson.get("list").getAsJsonArray().forEach(jsonElement -> onlinePlayerNames.add(jsonElement.getAsString()));

            String version = jsonObject.get("version").getAsString();
            boolean online = jsonObject.get("online").getAsBoolean();
            int protocol = jsonObject.get("protocol").getAsInt();
            String hostname = jsonObject.get("hostname").getAsString();
            String icon = jsonObject.has("icon") ? jsonObject.get("icon").getAsString() : null;
            return new BFServerInfo(ip, port, motd.toString(), onlinePlayers, maxPlayers, onlinePlayerNames.toArray(new String[0]), version, online, protocol, hostname, icon);
        }
    }
}
