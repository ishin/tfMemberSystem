package com.organ.service.adm;

import java.util.List;

import com.organ.model.TPosition;

public interface PositionService {

	List getByOrgan(Integer organId);
	void del(Integer id);
	TPosition save(String name, Integer organId);
}
