package com.organ.dao.member.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import com.organ.common.BaseDao;
import com.organ.dao.member.TextCodeDao;
import com.organ.model.TextCode;

/**
 * 短信验证码
 * @author hao_dy
 * @date 2017/01/04
 * @since jdk1.7
 */
public class TextCodeDaoImpl extends BaseDao<TextCode, Long> implements TextCodeDao {

	@Override
	public TextCode getTextCode(String phone) {
		try {
			Criteria ctr = getCriteria();
			ctr.add(Restrictions.eq("phoneNum", phone));
			
			List list = ctr.list();
			
			if (list.size() > 0) {
				return (TextCode) list.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	@Override
	public int deleteTextCode(String phone) {
		try {
			String hql = "delete TextCode T where T.phoneNum='" + phone + "'";
			int ret = delete(hql);
			
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return 0;
	}

	@Override
	public void saveTextCode(TextCode stc) {
		try {
			save(stc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	
}
