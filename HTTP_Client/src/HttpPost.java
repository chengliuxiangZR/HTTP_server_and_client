import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HttpPost {
    //请求URL
    public String url;
    //请求参数
    private Map<String,String> mParamsMap=new HashMap<String,String>();
    //客户端Socket
    Socket mSocket;
    public HttpPost(String url){
        this.url=url;
    }
    public void addParam(String key,String value){
        mParamsMap.put(key, value);
    }
    public void execute(){
        try {
            mSocket=new Socket(this.url,8000);
            PrintStream outputStream=new PrintStream(mSocket.getOutputStream());
            BufferedReader inputStream=new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            final String boundary="my_boundary_123";
            //写入header
            writeHeader(boundary,outputStream);
            //写入参数
            writeParams(boundary,outputStream);
            //等待返回数据
            waitResponse(inputStream);
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void writeHeader(String boundary,PrintStream outputStream){
        outputStream.println("POST /api/login HTTP/1.1");
        outputStream.println("content-length:123");
        outputStream.println("Host:"+this.url+":"+8000);
        outputStream.println("Content-Type:multipart/form-data;boundary="+boundary);
        outputStream.println("User-Agent:android");
        outputStream.println();
    }
    private void writeParams(String boundary,PrintStream outputStream){
        Iterator<String> paramsKeySet=mParamsMap.keySet().iterator();
        while(paramsKeySet.hasNext()){
            String paramName=paramsKeySet.next();
            outputStream.println("--"+boundary);
            outputStream.println("Content-Disposition:form-data; name="+paramName);
            outputStream.println();
            outputStream.println(mParamsMap.get(paramName));
        }
        outputStream.println("--"+boundary+"--");
    }
    private void waitResponse(BufferedReader inputStream) throws IOException{
        System.out.println("请求结果：");
        String responseLine=inputStream.readLine();
        while(responseLine==null||!responseLine.contains("HTTP")){
            responseLine=inputStream.readLine();
        }
        while((responseLine=inputStream.readLine())!=null){
            System.out.println(responseLine);
        }
    }

    public static void main(String[] args) {
        HttpPost httpPost=new HttpPost("118.117.51.194");
        httpPost.addParam("username","mr.simple");
        httpPost.addParam("pwd","my_pwd123");
        httpPost.execute();
    }

}
