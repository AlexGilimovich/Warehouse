package com.itechart.warehouse.service.services;

import com.itechart.warehouse.dto.ActDTO;
import com.itechart.warehouse.dto.ActSearchDTO;
import com.itechart.warehouse.entity.Act;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;

import java.util.List;

/**
 * Service for managing acts.
 * Provides basic operations with acts such as searching, creation, updating, deleting.
 */
public interface ActService {
    List<Act> findAllActs(int firstResult, int maxResults) throws DataAccessException;

    Act findActById(Long id) throws DataAccessException, IllegalParametersException;

    List<Act> findActsForGoods(Long goodsId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException;

    List<Act> findActsForCompany(Long companyId, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException;

    List<Act> findActsForCompanyByCriteria(Long companyId, ActSearchDTO actSearchDTO, int firstResult, int maxResults) throws DataAccessException, IllegalParametersException;

    Act createAct(ActDTO actDTO) throws DataAccessException, IllegalParametersException;

    Act updateAct(Long id, ActDTO actDTO) throws DataAccessException, IllegalParametersException;

    void deleteAct(Long id) throws DataAccessException, IllegalParametersException;

    boolean isAcExists(Long id) throws DataAccessException, IllegalParametersException;

}