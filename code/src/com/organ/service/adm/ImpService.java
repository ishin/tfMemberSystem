package com.organ.service.adm;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Iterator;

import javax.servlet.http.Part;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.SessionFactory;

import com.organ.common.Constants;
import com.organ.dao.adm.ImpDao;
import com.organ.model.ImpUser;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ImpService {

	private String status = "";
	private ImpUser user = null;
	private ImpDao impDao = null;
	
	public ImpService(SessionFactory factory) {

		this.impDao = new ImpDao(factory);
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject handleXls(Part part) {

		JSONObject js = new JSONObject();
		js.put(Constants.GOOD, new JSONArray());
		js.put(Constants.WELL, new JSONArray());
		js.put(Constants.BAD, new JSONArray());
		
		try {
			HSSFWorkbook workbook = new HSSFWorkbook(part.getInputStream());
			HSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> ri = sheet.iterator();
			if (ri.hasNext()){
				ri.next();
				while(ri.hasNext()) {
					Row row = ri.next();
					ImpUser user = rowToUser(row);
					if (this.handleUser(user)) {
						JSONArray ja = (JSONArray)js.get(this.getStatus());
						ja.add(this.getJson());
					}
					else {
						js.put("status", 3);//数据库错
						break;
					}
				}
				if (js.get("status") == null) {
					js.put("status", 0);//文件解析正常
				}
			}
			else {
				js.put("status", 4);//文件格式错
			}
		} catch (IOException e) {
			e.printStackTrace();
			js.put("status", 2);//文件IO错
		}
		
		return this.clean(js);
	}
	
	public JSONObject handleXlsx(Part part) {
		
		JSONObject js = new JSONObject();
		js.put(Constants.GOOD, new JSONArray());
		js.put(Constants.WELL, new JSONArray());
		js.put(Constants.BAD, new JSONArray());
		
		try {
			XSSFWorkbook workbook = new XSSFWorkbook(part.getInputStream());
			XSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> ri = sheet.iterator();
			if (ri.hasNext()){
				ri.next();
				while(ri.hasNext()) {
					Row row = ri.next();
					ImpUser user = rowToUser(row);
					if (this.handleUser(user)) {
						JSONArray ja = (JSONArray)js.get(this.getStatus());
						ja.add(this.getJson());
					}
					else {
						js.put("status", 3);//数据库错
						break;
					}
				}
				if (js.get("status") == null) {
					js.put("status", 0);//文件解析正常
				}
			}
			else {
				js.put("status", 4);//文件格式错
			}
		} catch (IOException e) {
			e.printStackTrace();
			js.put("status", 2);//文件IO错
		}
		
		return this.clean(js);
	}

	public JSONObject clean(JSONObject js) {
		
		while(true) {

			boolean edit = false;
			
			JSONArray jbad = (JSONArray)js.get(Constants.BAD);
			JSONArray jgood = (JSONArray)js.get(Constants.GOOD);
			
			Iterator it = jbad.iterator();
			while (it.hasNext()) {
				JSONObject j = (JSONObject)it.next();
				String branch = (String)j.get("branch");
				String manager = (String)j.get("manager");
				if (manager.indexOf("##") == 0) {
					if (this.isBranchExist(branch, js) || this.isManagerExist(manager.substring(2), js)) {
						j.put("manager", manager.substring(2));
						jgood.add(j);
						jbad.remove(j);
						edit = true;
						break;
					}
				}
			}
			
			if (!edit) break;
		}
		
		return js;
	}

	private boolean isManagerExist(String manager, JSONObject js) {
		
		JSONArray jgood = (JSONArray)js.get(Constants.GOOD);
		
		Iterator it = jgood.iterator();
		
		while (it.hasNext()) {
			JSONObject j = (JSONObject)it.next();
			if (manager.equals((String)j.get("name"))) return true;
		}

		return false;
	}
	
	private boolean isBranchExist(String branch, JSONObject js) {
		
		JSONArray jgood = (JSONArray)js.get(Constants.GOOD);
		
		Iterator it = jgood.iterator();
		
		while (it.hasNext()) {
			JSONObject j = (JSONObject)it.next();
			if (branch.equals((String)j.get("branch"))) return true;
		}

		return false;
	}
	
	public boolean handleUser(ImpUser user) {
	
		this.user = user;
		impDao.setUser(user);
		boolean result = true;
		
		try {

			// 如果数据不完整
			if (this.testBlank()) {
				this.status = Constants.BAD;
			}
			
			// 如果人员已存在
			else if (impDao.testExist()) {
				this.status = Constants.WELL;
			}
					
			// 如果性别错
			/*else if (!testSex()){
				this.status = Constants.BAD;
				this.user.setSex("##" + this.user.getSex());
			}*/
			
			// 如果部门存在
			else if (impDao.testBranch()){
				this.status = Constants.GOOD;
			}
			
			// 如果将作为部门经理的人员存在
			else if (user.getName().equals(user.getManager()) || impDao.testManager()) {
					this.status = Constants.GOOD;
			}
				
			else {
				this.status = Constants.BAD;
				this.user.setManager("##" + this.user.getManager());
			}

		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		
		return result;
	}
	
	private boolean testSex() {
		if ("男".equals(user.getSex()) || "女".equals(user.getSex())) {
			return true;
		}
		return false;
	}
	
	private boolean testBlank() {
	
		if ("".equals(user.getMobile())
				|| "".equals(this.user.getName())
				|| "".equals(this.user.getWorkno())
			//	|| "".equals(this.user.getSex())
				|| "".equals(this.user.getBranch())
				|| "".equals(this.user.getManager())) {

			return true;
		}
		
		return false;
	}
	
	private ImpUser rowToUser(Row row) {
		
		ImpUser user = new ImpUser(); 

		Cell cell = row.getCell(0);
		user.setMobile(getCellValue(row.getCell(0)));
		user.setName(getCellValue(row.getCell(1)));
		user.setWorkno(getCellValue(row.getCell(2)));
		user.setSex(getCellValue(row.getCell(3)));
		user.setBranch(getCellValue(row.getCell(4)));
		user.setManager(getCellValue(row.getCell(5)));
		user.setPosition(getCellValue(row.getCell(6)));
		user.setTelephone(getCellValue(row.getCell(7)));
		user.setEmail(getCellValue(row.getCell(8)));

		return user;
	}
	
	private String getCellValue(Cell cell) {
		
		if (cell == null) return "";
		DecimalFormat df = new DecimalFormat("#");

		String value = null;
		switch(cell.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC:
			value = String.valueOf(df.format(cell.getNumericCellValue()));
			break;
		case Cell.CELL_TYPE_STRING:
			value = cell.getStringCellValue();
			break;
		default:
			value = "";
		}
		
		return value;
	}

	public String getStatus() {
		return status;
	}
	
	public JSONObject getJson() {
		
		JSONObject js = new JSONObject();
		
		js.put("mobile", this.user.getMobile());
		js.put("name", this.user.getName());
		js.put("workno", this.user.getWorkno());
		js.put("sex", this.user.getSex());
		js.put("branch", this.user.getBranch());
		js.put("manager", this.user.getManager());
		js.put("position", this.user.getPosition());
		js.put("telephone", this.user.getTelephone());
		js.put("email", this.user.getEmail());
		
		return js;
	}
}