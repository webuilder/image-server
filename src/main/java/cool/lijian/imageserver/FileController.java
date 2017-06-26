package cool.lijian.imageserver;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;

/**
 * 
 * @author lijian
 *
 */
@SpringBootApplication
@Controller
public class FileController {

	private static final Logger logger = LoggerFactory.getLogger(FileController.class);

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SpringApplication.run(FileController.class, args);
	}

	@Autowired
	private MultipartResolver multipartResolver;

	@Autowired
	private NamingStrategy namingStrategy;

	@Value("${image_server.storage_path}")
	private String storagePath;

	@Value("${image_server.zoom_path}")
	private String zoomPath;

	@Autowired
	private ZoomService zoomService;

	@RequestMapping("/**")
	public void image(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String fileId = request.getServletPath();
		if ("/".equals(fileId)) {
			showIndex(response);
			return;
		}
		if (fileId.charAt(0) == '/') {
			fileId = fileId.substring(1);
		}
		if (fileId.indexOf("..") >= 0) {
			logger.debug("Invalid request <{}>", fileId);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		File file = new File(storagePath, fileId);
		if (!file.exists()) {
			logger.debug("<{}> not exist.", fileId);
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		String m = request.getParameter("m");
		String w = request.getParameter("w");
		String h = request.getParameter("h");

		if (m == null) {
			// Read orginal file
			logger.debug("read file <{}>", fileId);
			OutputStream out = response.getOutputStream();
			try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
				IOUtils.copy(in, out);
			}
		} else {
			// Read zoomed image
			ZoomMode zm = ZoomMode.MODE_1;
			int width = 0;
			int height = 0;
			try {
				zm = ZoomMode.valueOf(Integer.parseInt(m));
				width = w == null ? 0 : Integer.parseInt(w);
				height = h == null ? 0 : Integer.parseInt(h);
			} catch (Exception e) {
				logger.debug("Invalid request <{}>, m = <{}>, w = <{}>, h = <{}>", fileId, m, w, h);
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}

			if (width < 0 || height < 0 || (width == 0 && height == 0)) {
				logger.debug("Invalid request <{}>, m = <{}>, w = <{}>, h = <{}>", fileId, m, w, h);
				response.sendError(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			String zoomedFileId = zoomService.zoom(fileId, zm, width, height);
			File zoomedFile = new File(zoomPath, zoomedFileId);
			if (!zoomedFile.exists()) {
				logger.debug("zoomed file <{}> not exist.", fileId);
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}

			logger.debug("read zoomed file <{}>", fileId);
			OutputStream out = response.getOutputStream();
			try (InputStream in = new BufferedInputStream(new FileInputStream(zoomedFile))) {
				IOUtils.copy(in, out);
			}
		}
	}

	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public @ResponseBody String uploadForm() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (InputStream in = FileController.class.getClassLoader().getResourceAsStream("upload_form.html");) {
			IOUtils.copy(in, baos);
		}
		System.out.println(multipartResolver);
		return new String(baos.toByteArray(), Charset.forName("utf-8"));
	}

	@RequestMapping(value = "/upload_api", method = RequestMethod.POST)
	public @ResponseBody String uploadApi(@RequestParam("file") MultipartFile file) throws Exception {
		if (!file.isEmpty()) {
			byte[] bytes = file.getBytes();
			String originalFileName = file.getOriginalFilename();
			String fileId = namingStrategy.createFileId(bytes, originalFileName);
			File savedFile = new File(storagePath, fileId);
			File parentDir = savedFile.getParentFile();
			if (!parentDir.exists()) {
				parentDir.mkdirs();
			}
			file.transferTo(savedFile);
			return fileId;
		}
		return "";
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody String upload(@RequestParam("file") MultipartFile file) throws Exception {
		if (!file.isEmpty()) {
			String fileId = uploadApi(file);
			String html = "FileId is <a href='/" + fileId + "'>" + fileId + "</a>";
			return html;
		}
		return "";
	}

	@Value("${image_server.index}")
	private String indexPage;

	private void showIndex(HttpServletResponse response) throws Exception {
		InputStream in = getClass().getClassLoader().getResourceAsStream(indexPage);
		OutputStream out = response.getOutputStream();
		IOUtils.copy(in, out);
	}
}
