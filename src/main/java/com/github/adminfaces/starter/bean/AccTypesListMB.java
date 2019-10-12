package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.infra.model.Filter;
import com.github.adminfaces.starter.model.Reception;
import com.github.adminfaces.starter.service.ReceptionService;
import com.github.adminfaces.starter.util.Db;
import com.github.adminfaces.template.exception.BusinessException;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static com.github.adminfaces.starter.util.Utils.addDetailMessage;

/**
 * Created by rmpestano on 12/02/17.
 */

@Named
@ViewScoped
public class AccTypesListMB implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Inject
    ReceptionService receptionService;

    String opno;

    LazyDataModel<Reception> patients;

    Filter<Reception> filter = new Filter<>(new Reception());

    List<Reception> selectedPatients; //patients selected in checkbox column

    List<Reception> filteredValue;// datatable filteredValue attribute (column filters)

    @PostConstruct
    public void initDataModel() {
        patients = new LazyDataModel<Reception>() {
            @Override
            public List<Reception> load(int first, int pageSize,
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
                List<Reception> list = receptionService.paginate(filter);
                setRowCount((int) receptionService.count(filter));
                return list;
            }

            @Override
            public int getRowCount() {
                return super.getRowCount();
            }

            @Override
            public Reception getRowData(String key) {
                return receptionService.findByOpno(key);
            }
        };
    }

    public void clear() {
        filter = new Filter<Reception>(new Reception());
    }

    public List<String> completeModelOpno(String query) {
        List<String> result = receptionService.getOpno(query);
        return result;
    }

    public List<String> completeModel(String query) {
        List<String> result = receptionService.getNames(query);
        return result;
    }

    public void findPatientById(String opno) {
        if (opno == null) {
            throw new BusinessException("Provide User Account Type ID to load");
        }
        selectedPatients.add(receptionService.findByOpno(opno));
    }

    public void delete() {
        int numPatients = 0;
        for (Reception selectedCar : selectedPatients) {
            numPatients++;
            receptionService.remove(selectedCar);
        }
        selectedPatients.clear();
        addDetailMessage(numPatients + " User Account Type deleted successfully!");
    }

    public List<Reception> getFilteredValue() {
        return filteredValue;
    }

    public void setFilteredValue(List<Reception> filteredValue) {
        this.filteredValue = filteredValue;
    }

    public LazyDataModel<Reception> getPatients() {
        return patients;
    }

    public void setPatients(LazyDataModel<Reception> patients) {
        this.patients = patients;
    }

    public Filter<Reception> getFilter() {
        return filter;
    }

    public void setFilter(Filter<Reception> filter) {
        this.filter = filter;
    }

    public List<Reception> getSelectedPatients() {
        return selectedPatients;
    }

    public void setSelectedPatients(List<Reception> selectedPatients) {
        this.selectedPatients = selectedPatients;
    }

    public String getOpno() {
        return opno;
    }

    public void setOpno(String opno) {
        this.opno = opno;
    }
}
