import com.idrsolutions.image.png.PngEncoder;
import psd.BinaryReader;
import psd.PsdReader;

import java.io.*;

public class Main {
    public static void main(String[]args) throws IOException {
        PsdReader psdReader = new PsdReader();
        BinaryReader binaryReader = new BinaryReader();

        FileInputStream fis = new FileInputStream("C:/Users/cksgu/git/psd/src/main/resources/alt.psd");
        BufferedInputStream bis = new BufferedInputStream(fis);

        binaryReader.setInput(bis);
        System.out.println(binaryReader.getBinary());
        bis = new BufferedInputStream(new FileInputStream("C:/Users/cksgu/git/psd/src/main/resources/alt.psd"));

        psdReader.open(bis);
        psdReader.run();

        PngEncoder pngEncoder = new PngEncoder();

        try {
            // PNG 파일로 저장하고 싶은 경우
            FileOutputStream fos = new FileOutputStream("output.png");
            pngEncoder.write(psdReader.getPsdLayers()[0].getFrame(), fos);
            fos.close();

            // 여기서 PNG 파일이 생성됩니다.
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(psdReader.toString());
    }
}