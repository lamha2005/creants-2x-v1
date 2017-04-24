package com.creants.creants_2x.socket.gate.protocol.serialization;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.creants.creants_2x.socket.gate.entities.QAntArray;
import com.creants.creants_2x.socket.gate.entities.QAntObject;
import com.creants.creants_2x.socket.gate.entities.IQAntArray;
import com.creants.creants_2x.socket.gate.entities.IQAntObject;

/**
 * @author Lamhm
 *
 */
public interface IQAntDataSerializer {
	byte[] object2binary(IQAntObject object);


	byte[] array2binary(IQAntArray array);


	IQAntObject binary2object(byte[] byteArray);


	IQAntArray binary2array(byte[] byteArray);


	String object2json(Map<String, Object> map);


	String array2json(List<Object> list);


	IQAntObject json2object(String jsonString);


	IQAntArray json2array(String jsonString);


	QAntObject resultSet2object(ResultSet resultSet) throws SQLException;


	QAntArray resultSet2array(ResultSet resultSet) throws SQLException;
}
