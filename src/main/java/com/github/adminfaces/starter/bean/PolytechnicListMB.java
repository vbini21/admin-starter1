package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.infra.model.Filter;
import com.github.adminfaces.starter.model.Polytechnic;
import com.github.adminfaces.starter.service.PolytechnicService;
import com.github.adminfaces.starter.util.Db;
import static com.github.adminfaces.starter.util.Db.ps;
import static com.github.adminfaces.starter.util.Db.rs;
import com.github.adminfaces.template.exception.BusinessException;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 * Created by rmpestano on 12/02/17.
 */

@Named
@ViewScoped
public class PolytechnicListMB implements Serializable {
private static Logger logger = Logger.getLogger(PolytechnicListMB.class);
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Inject
    PolytechnicService polytechnicService;

    String opno;

    LazyDataModel<Polytechnic> patients;

    Filter<Polytechnic> filter = new Filter<>(new Polytechnic());

    List<Polytechnic> selectedPatients; //patients selected in checkbox column

    List<Polytechnic> filteredValue;// datatable filteredValue attribute (column filters)

    @PostConstruct
    public void initDataModel() {
        patients = new LazyDataModel<Polytechnic>() {
            @Override
            public List<Polytechnic> load(int first, int pageSize,
                    String sortField, SortOrder sortOrder,
                    Map<String, Object> filters) {
                com.github.adminfaces.starter.infra.model.SortOrder order = null;
                if (sortOrder != null) {
                    order = sortOrder.equals(SortOrder.ASCENDING) ? com.github.adminfaces.starter.infra.model.SortOrder.ASCENDING
                            : sortOrder.equals(SortOrder.DESCENDING) ? com.github.adminfaces.starter.infra.model.SortOrder.DESCENDING
                            : com.github.adminfaces.starter.infra.model.SortOrder.UNSORTED;
                }
                filter.setFirst(first).setPageSize(pageSize)
                        .setSortField(sortField).setSortOrder(order)
                        .setParams(filters);
                List<Polytechnic> list = polytechnicService.paginate(filter);
                setRowCount((int) polytechnicService.count(filter));
                return list;
            }

            @Override
            public int getRowCount() {
                return super.getRowCount();
            }

            @Override
            public Polytechnic getRowData(String key) {
                return polytechnicService.findByOpno(key);
            }
        };
    }

    public void clear() {
        filter = new Filter<Polytechnic>(new Polytechnic());
    }

    public List<String> completeModelOpno(String query) {
        List<String> result = polytechnicService.getOpno(query);
        return result;
    }

    public List<String> completeModel(String query) {
        List<String> result = polytechnicService.getNames(query);
        return result;
    }

    public List<String> completeModelPhone(String query) {
        List<String> result = polytechnicService.getPhones(query);
        return result;
    }
    
    public void findPatientById(String opno) {
        if (opno == null) {
            throw new BusinessException("Provide Polytechnic ID to load");
        }
        selectedPatients.add(polytechnicService.findByOpno(opno));
    }

    public void delete() {
        int numPatients = 0;
        for (Polytechnic selectedCar : selectedPatients) {
            numPatients++;
            polytechnicService.remove(selectedCar);
        }
        selectedPatients.clear();
        addDetailMessage(numPatients + " Polytechnic deleted successfully!");
    }

    public List<Polytechnic> getFilteredValue() {
        return filteredValue;
    }

    public void setFilteredValue(List<Polytechnic> filteredValue) {
        this.filteredValue = filteredValue;
    }

    public LazyDataModel<Polytechnic> getPatients() {
        return patients;
    }

    public void setPatients(LazyDataModel<Polytechnic> patients) {
        this.patients = patients;
    }

    public Filter<Polytechnic> getFilter() {
        return filter;
    }

    public void setFilter(Filter<Polytechnic> filter) {
        this.filter = filter;
    }

    public List<Polytechnic> getSelectedPatients() {
        return selectedPatients;
    }

    public void setSelectedPatients(List<Polytechnic> selectedPatients) {
        this.selectedPatients = selectedPatients;
    }

    public String getOpno() {
        return opno;
    }

    public void setOpno(String opno) {
        this.opno = opno;
    }
}
