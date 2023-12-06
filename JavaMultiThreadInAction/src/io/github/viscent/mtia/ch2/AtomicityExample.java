package io.github.viscent.mtia.ch2;

public class AtomicityExample {

    private HostInfo hostInfo;

    public void updateHostInfo(String ip, int port) {
        hostInfo.setIp(ip);
        hostInfo.setPort(port);
    }

    public void updateHostInfo2(String ip, int port) {
        HostInfo hostInfo = new HostInfo(ip, port);
        this.hostInfo = hostInfo;
    }

    public void connectToHost() {
        String ip = hostInfo.getIp();
        int port = hostInfo.getPort();
        connectToHost(ip, port);
    }

    private void connectToHost(String ip, int port) {

    }

    public static class HostInfo {
        private String ip;

        private int port;

        HostInfo(String ip, int port){
          this.ip = ip;
          this.port = port;
        }
        
        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }
}
