import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import jsky.coords.wcscon;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


public class Gal2fk5{

    private static Logger logger  = Logger.getLogger(Gal2fk5.class);
    private static String logFilename= "logger.config";
    private static File logFile = new File(logFilename);

    public static void main (String[] args)  throws IOException, AsciiDataFileFormatException {

	DecimalFormat coord = new DecimalFormat("0.0000");

	//  Configure the logger
	InputStream is = ClassLoader.getSystemResourceAsStream(logFilename);
   	inputStreamToFile(is, logFilename);
	PropertyConfigurator.configure(logFilename);

	wcscon convert = new wcscon();
	if ( args.length == 1 ) {
	    String lbFilename = args[0];
	    AsciiDataFileReader input = new AsciiDataFileReader(lbFilename);
	    double[] l = input.getDblCol(0);
	    double[] b = input.getDblCol(1);
	    int nPoints = l.length;
	    double[] ra = new double[nPoints];
	    double[] dec = new double[nPoints];
	    for ( int i=0; i < nPoints; i++ ) {
		Point2D.Double lb = new Point2D.Double(l[i], b[i]);
		Point2D.Double radec = convert.gal2fk5(lb);
		ra[i] = radec.getX();
		dec[i] = radec.getY();
	    }
	    String raDecFilename = "fk5Coords.txt";
	    AsciiDataFileWriter output = new AsciiDataFileWriter(raDecFilename);
	    output.writeData(new String[] {}, ra, dec);
	}
	else if ( args.length == 2 ) {
	    double l = (Double.valueOf(args[0])).doubleValue();
	    double b = (Double.valueOf(args[1])).doubleValue();
	    Point2D.Double lb = new Point2D.Double(l, b);
	    Point2D.Double radec = convert.gal2fk5(lb);
	    logger.info(coord.format(radec.getX())+" "+coord.format(radec.getY()));
	}
	else {
	    logger.info("Usage: java Gal2fk5 l b (in degrees)");
	    logger.info("Usage: java Gal2fk5 lbFile.txt");
	    logFile.delete();
	    System.exit(-1);
	}
	logFile.delete();
    }

    public static void inputStreamToFile(InputStream io, String fileName) throws IOException {
	
	FileOutputStream fos = new FileOutputStream(fileName);
	byte[] buf = new byte[256];
	int read = 0;
	while ((read = io.read(buf)) > 0) {
	    fos.write(buf, 0, read);
	}
	fos.flush();
	fos.close();
    }

}