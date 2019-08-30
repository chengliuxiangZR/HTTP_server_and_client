import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.Buffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DeliverThread extends Thread{
    Socket mClientSocket;
    //输入流，接受客服端的Socket输入
    BufferedReader mInputStream;
    //输出流
    PrintStream moutputStream;
    //请求方法：GET,POST等
    String httpMethod;
    //子路径
    String subPath;
    //报文分隔符
    String boundary;
    //请求参数
    Map<String,String> mParams=new HashMap<String, String>();
    //请求头参数
    Map<String,String> mHeader=new HashMap<String,String>();
    //是否已经解析完了header
    boolean isParseHeader=false;
    public DeliverThread(Socket socket){
        mClientSocket=socket;
    }

    @Override
    public void run() {
        try {
            mInputStream=new BufferedReader(new InputStreamReader(mClientSocket.getInputStream()));
            moutputStream=new PrintStream(mClientSocket.getOutputStream());
            //解析请求
            parseRequest();
            //返回Response
            handleResponse();
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            //关闭流和Socket
            try {
                moutputStream.close();
                mInputStream.close();
                mClientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void parseRequest(){
        String line;
        try {
            int lineNum=0;
            while ((line=mInputStream.readLine())!=null){
                //解析请求行
                if(lineNum==0){
                    parseRequestLine(line);
                }
                //判断是否是数据的结束行
                if(isEnd(line)){
                    break;
                }
                //解析header参数
                if(lineNum!=0&&!isParseHeader){
                    parseHeaders(line);
                }
                //解析请求参数
                if(isParseHeader){
                    parseRequestParams(line);
                }
                lineNum++;
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private boolean isEnd(String line){
        if(line.equals("")){
            return true;
        }else {
            return false;
        }
    }
    //解析请求行
    private void parseRequestLine(String lineOne){
        String[] tempStrings=lineOne.split(" ");
        httpMethod=tempStrings[0];
        subPath=tempStrings[1];
        System.out.println("请求方式是："+tempStrings[0]);
        System.out.println("子路径是："+tempStrings[1]);
        System.out.println("Http版本："+tempStrings[2]);
    }
    //解析header,参数为每个header的字符串
    private void parseHeaders(String headerLine){
        if(headerLine.equals("")){
            isParseHeader=true;
            System.out.println("----->header解析完成\n");
            return;
        }else if (headerLine.contains("boundary")){
            boundary=parseSecondField(headerLine);
            System.out.println("分隔符："+boundary);
        }else {
            parseHeaderParam(headerLine);
        }
    }
    //解析header中的第二个参数
    private String parseSecondField(String line){
        String[] headerArray=line.split(";");
        parseHeaderParam(headerArray[0]);
        if(headerArray.length>1){
            return headerArray[1].split("=")[1];
        }
        return "";
    }
    //解析单个header
    private void parseHeaderParam(String headerLine){
        String[] keyvalue=headerLine.split(":");
        mHeader.put(keyvalue[0].trim(),keyvalue[1].trim());
        System.out.println("header参数名；"+keyvalue[0].trim()+"，参数值："+keyvalue[1].trim());
    }
    //解析请求参数
    private void parseRequestParams(String paramLine)throws IOException{
        if(paramLine.equals("--"+boundary)){
            String ContentDisponsition=mInputStream.readLine();
            String paramName=parseSecondField(ContentDisponsition);
            mInputStream.readLine();
            String paramValue=mInputStream.readLine();
            mParams.put(paramName,paramValue);
            System.out.println("参数名："+paramName+"，参数值："+paramValue);
        }
    }
    //返回结果
    private void handleResponse(){
        //模拟处理耗时
        sleep();
        //向输出流写数据
        moutputStream.println("HTTP/1.1 200 OK");
        moutputStream.println("Content-Type: application/json");
        moutputStream.println();
        moutputStream.println("{\"stCode\":\"success\"}");
    }
    private void sleep(){
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
