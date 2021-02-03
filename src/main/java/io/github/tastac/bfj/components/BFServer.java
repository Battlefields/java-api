package io.github.tastac.bfj.components;

/**
 * <p>An official server that can be connected to.</p>
 *
 * @author Ocelot
 */
public class BFServer
{
    private final String ip;
    private final String status;

    public BFServer(String ip, String status)
    {
        this.ip = ip;
        this.status = status;
    }

    /**
     * @return The ip address of the server
     */
    public String getIp()
    {
        return ip;
    }

    /**
     * @return The response status of the server
     */
    public String getStatus()
    {
        return status;
    }

    /**
     * @return Whether or not this server is up
     */
    public boolean isUp()
    {
        return "green".equals(this.status);
    }

    /**
     * @return Whether or not this server is down
     */
    public boolean isDown()
    {
        return "red".equals(this.status);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BFServer bfServer = (BFServer) o;
        return this.ip.equals(bfServer.ip);
    }

    @Override
    public int hashCode()
    {
        return this.ip.hashCode();
    }

    @Override
    public String toString()
    {
        return "BFServer{" +
                "ip='" + this.ip + '\'' +
                ", status='" + this.status + '\'' +
                '}';
    }
}
