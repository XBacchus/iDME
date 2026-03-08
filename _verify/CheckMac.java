import java.net.*;
public class CheckMac {
  public static void main(String[] args) throws Exception {
    InetAddress addr = InetAddress.getLocalHost();
    System.out.println("localHost=" + addr + " class=" + addr.getClass().getName());
    NetworkInterface ni = NetworkInterface.getByInetAddress(addr);
    System.out.println("ni=" + (ni==null?"null":ni.getName()+"/"+ni.getDisplayName()));
    if (ni != null) {
      byte[] mac = ni.getHardwareAddress();
      System.out.println("mac=" + (mac==null?"null":toHex(mac)));
    }
  }
  static String toHex(byte[] b){StringBuilder sb=new StringBuilder();for(int i=0;i<b.length;i++){if(i>0)sb.append(":");sb.append(String.format("%02X",b[i]));}return sb.toString();}
}
