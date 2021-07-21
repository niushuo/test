package test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.googlecode.mp4parser.util.Logger;

import ucar.nc2.dataset.conv.IFPSConvention;


public class ZipUtil {
	private static Logger logger = Logger.getLogger(ZipUtil.class);

	public static void compressFile(String fileforder,String out) {
		FileOutputStream fos = null;
		CheckedOutputStream cos = null;
		ZipOutputStream zos = null;
		BufferedOutputStream outStream = null;
		try {
			fos = new FileOutputStream(out);
			cos = new CheckedOutputStream(fos, new Adler32());
			zos = new ZipOutputStream(cos);
			outStream = new BufferedOutputStream(zos);
			File forder = new File(fileforder);
			File[] files = forder.listFiles();

			for(File file : files){
				BufferedInputStream bis = null;
				try {
					bis = new BufferedInputStream(new FileInputStream(file));
					zos.putNextEntry(new ZipEntry(file.getName()));
					byte[] bytebuffer = new byte[1024];
					int len = 0;
					while ((len = bis.read(bytebuffer)) != -1){
						outStream.write(bytebuffer,0,len);
					}
				} catch (Exception e){
					e.printStackTrace();
				} finally {
					if(bis != null){
						bis.close();
					}
					outStream.flush();
				} 
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(outStream != null){
				try{
					outStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(zos != null){
				try{
					zos.close();
				}catch ( IOException e) {
					e.printStackTrace();
				}
			}
			if(fos != null){
				try {
					fos.close();
				}catch (IOException e){
					e.printStackTrace();
				}
			}
		}
	}
	public static boolean upzip(String file,String out) {
		FileInputStream fis =null;
		CheckedInputStream cis = null;
		ZipInputStream zis = null;
		try{
			fis = new FileInputStream(file);
			cis = new CheckedInputStream(fis, new Adler32());
			ZipEntry ze;
			while((ze = zis.getNextEntry()) != null){//Ñ­»·¶ÁÎÄ¼þ
				if(ze.isDirectory()){
					return false;
				}
				String fname = new String(out + ze.getName());
				FileOutputStream outputStream = null;
				try{
					outputStream = new FileOutputStream(fname);
					byte[] buffer = new byte[1024];
					int n;
					while((n = zis.read(buffer,0,1024)) != -1){
						outputStream.write(buffer,0,n);
					}
				} catch (Exception e) {
					return false;
				} finally{
					if (outputStream != null){
						outputStream.close();
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally {
			if(zis != null){
				try{
					zis.close();
				}catch (IOException e){
					e.printStackTrace();
				}
			}
			if(cis != null){
				try{
					cis.close();
				}catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(fis != null){
				try{
					fis.close();
				}catch (IOException e){
					e.printStackTrace();
				}
			}
			File zipfile =  new File(file);
			zipfile.delete();
		}
		return true;	
	}
}
