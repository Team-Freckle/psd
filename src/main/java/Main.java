import psd.BinaryReader;
import psd.PsdManipulator;

import javax.imageio.ImageIO;
import java.io.*;

public class Main {
    public static void main(String[]args) throws IOException {
        System.out.println("registering..");
        PsdManipulator psdManipulator = new PsdManipulator();
        BinaryReader binaryReader = new BinaryReader();

        System.out.println("importing..");
        FileInputStream fis = new FileInputStream("C:/Users/cksgu/Desktop/alt.psd");
        BufferedInputStream bis = new BufferedInputStream(fis);

        System.out.println("binary running..");
        binaryReader.setInput(bis);
        System.out.println(binaryReader.getBinary(200000, 230000));
        bis = new BufferedInputStream(new FileInputStream("C:/Users/cksgu/Desktop/alt.psd"));

        System.out.println("psd running..");
        psdManipulator.open(bis);
        psdManipulator.run();

        System.out.println("creating..");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(psdManipulator.getPreview(), "png", baos);

        try {
            // PNG 파일로 저장하고 싶은 경우
            FileOutputStream fos = new FileOutputStream("C:/Users/cksgu/Desktop/output.png");
            fos.write(baos.toByteArray());
            fos.close();

            // 여기서 PNG 파일이 생성됩니다.
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(psdManipulator.toString());
    }
}