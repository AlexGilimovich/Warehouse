package com.itechart.warehouse.controller;

import com.itechart.warehouse.controller.error.RequestHandlingError;
import com.itechart.warehouse.controller.error.ValidationError;
import com.itechart.warehouse.controller.error.ValidationErrorBuilder;
import com.itechart.warehouse.controller.response.IdResponse;
import com.itechart.warehouse.controller.response.StatusEnum;
import com.itechart.warehouse.controller.response.StatusResponse;
import com.itechart.warehouse.dto.ActDTO;
import com.itechart.warehouse.dto.ActSearchDTO;
import com.itechart.warehouse.entity.Act;
import com.itechart.warehouse.entity.ActType;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.RequestHandlingException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.ActService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

import static com.itechart.warehouse.util.Host.origins;

/**
 * REST controller for handling requests to goods service.
 */
@CrossOrigin(origins = origins, maxAge = 3600)
@RestController
@RequestMapping(value = "/act")
@Validated
public class ActController {
    private ActService actService;
    private Logger logger = LoggerFactory.getLogger(ActController.class);

    @Autowired
    public void setActService(ActService actService) {
        this.actService = actService;
    }

    @RequestMapping(value = "list/{warehouseId}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ActDTO>> getActs(@PathVariable Long warehouseId,
                                                @RequestParam(defaultValue = "-1") int page,
                                                @RequestParam(defaultValue = "0") int count,
                                                HttpServletResponse response) throws DataAccessException, IllegalParametersException, RequestHandlingException {
        logger.info("Handling request for list of acts, page: {}, count: {}", page, count);
        List<ActDTO> acts = actService.findActsForWarehouse(warehouseId, (page - 1) * count, count);
        long actsCount = actService.getActsCount(warehouseId);
        response.addHeader("X-total-count", String.valueOf(actsCount));
        response.addHeader("Access-Control-Expose-Headers", "X-total-count");
        return new ResponseEntity<>(acts, HttpStatus.OK);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ActDTO> getActsDTOs(@PathVariable Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Handling request for act with id: {}", id);
        ActDTO act = actService.findActDTOById(id);
        return new ResponseEntity<>(act, HttpStatus.OK);
    }

    @RequestMapping(value = "acts/{goodsId}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ActDTO>> getActsForGoods(@PathVariable Long goodsId)
            throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Handling request for acts of goods with id {}", goodsId);
        return new ResponseEntity<>(actService.findActsForGoods(goodsId, -1, -1), HttpStatus.OK);
    }

    @RequestMapping(value = "/acts", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<ActType>> getActTypes() throws DataAccessException {
        logger.info("Handling request for units list");
        List<ActType> actTypes = actService.getActTypes();
        return new ResponseEntity<>(actTypes, HttpStatus.OK);
    }


    @RequestMapping(value = "/save", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<IdResponse> saveAct(@Valid @RequestBody ActDTO actDTO) throws DataAccessException, IllegalParametersException, RequestHandlingException, ResourceNotFoundException {
        logger.info("Handling request for saving new act using DTO: {}", actDTO);
        Act savedAct = actService.createAct(actDTO);
        if (savedAct != null)
            return new ResponseEntity<>(new IdResponse(savedAct.getId()), HttpStatus.CREATED);
        else throw new RequestHandlingException("Act was not stored");
    }

    @RequestMapping(value = "/save/{id}", method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StatusResponse> updateAct(@PathVariable(value = "id") Long id,
                                                    @Valid @RequestBody ActDTO actDTO) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Handling request for updating act with id: {} by DTO: {}", id, actDTO);
        actService.updateAct(id, actDTO);
        return new ResponseEntity<>(new StatusResponse(StatusEnum.UPDATED), HttpStatus.OK);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StatusResponse> deleteAct(@PathVariable(value = "id") Long id) throws DataAccessException, IllegalParametersException, ResourceNotFoundException {
        logger.info("Handling request for deleting act with id: {}", id);
        actService.deleteAct(id);
        return new ResponseEntity<>(new StatusResponse(StatusEnum.DELETED), HttpStatus.OK);
    }

    @RequestMapping(value = "/search/{warehouseId}", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ActDTO>> findActs(@PathVariable Long warehouseId,
                                                 @RequestParam(defaultValue = "-1") int page,
                                                 @RequestParam(defaultValue = "0") int count,
                                                 @RequestBody ActSearchDTO actSearchDTO,
                                                 HttpServletResponse response) throws DataAccessException, IllegalParametersException, RequestHandlingException {
        logger.info("Handling request for searching list of acts by field: {}, page: {}, count: {}", actSearchDTO, page, count);
        List<ActDTO> acts = actService.findActsForWarehouseByCriteria(warehouseId, actSearchDTO, (page - 1) * count, count);
        long c = actService.getCountOfActsForWarehouseByCriteria(warehouseId, actSearchDTO);
        response.addHeader("X-total-count", String.valueOf(c));
        response.addHeader("Access-Control-Expose-Headers", "X-total-count");
        return new ResponseEntity<>(acts, HttpStatus.OK);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public
    @ResponseBody
    ValidationError handleException(MethodArgumentNotValidException e) {
        return createValidationError(e);
    }

    private ValidationError createValidationError(MethodArgumentNotValidException e) {
        return ValidationErrorBuilder.fromBindingErrors(e.getBindingResult());
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(value = HttpStatus.CONFLICT)
    public
    @ResponseBody
    RequestHandlingError handleException(DataAccessException e) {
        RequestHandlingError dataAccessError = new RequestHandlingError();
        dataAccessError.setError(e.getMessage());
        return dataAccessError;
    }

    @ExceptionHandler(IllegalParametersException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public
    @ResponseBody
    RequestHandlingError handleException(IllegalParametersException e) {
        logger.error("Exception during request handling: {}", e.getMessage());
        RequestHandlingError illegalParametersError = new RequestHandlingError();
        illegalParametersError.setError(e.getMessage());
        return illegalParametersError;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public
    @ResponseBody
    RequestHandlingError handleException(HttpMessageNotReadableException e) {
        logger.error("Exception during request handling: {}", e.getMessage());
        RequestHandlingError illegalParametersError = new RequestHandlingError();
        illegalParametersError.setError("Message is syntactically incorrect");
        return illegalParametersError;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public
    @ResponseBody
    RequestHandlingError handleException(ResourceNotFoundException e) {
        logger.error("Exception during request handling: {}", e.getMessage());
        RequestHandlingError resourceNotFoundError = new RequestHandlingError();
        resourceNotFoundError.setError(e.getMessage());
        return resourceNotFoundError;
    }

    @ExceptionHandler(RequestHandlingException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public
    @ResponseBody
    RequestHandlingError handleException(RequestHandlingException e) {
        logger.error("Exception during request handling: {}", e.getMessage());
        RequestHandlingError requestHandlingError = new RequestHandlingError();
        requestHandlingError.setError(e.getMessage());
        return requestHandlingError;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    public
    @ResponseBody
    RequestHandlingError handleException(AccessDeniedException e) {
        logger.error("Exception during request handling: {}", e.getMessage());
        RequestHandlingError requestHandlingError = new RequestHandlingError();
        requestHandlingError.setError(e.getMessage());
        return requestHandlingError;
    }
}
