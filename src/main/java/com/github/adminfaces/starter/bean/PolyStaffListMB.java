package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.infra.model.Filter;
import com.github.adminfaces.starter.model.PolyStaff;
import com.github.adminfaces.starter.service.PolyStaffService;
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

/**
 * Created by rmpestano on 12/02/17.
 */

@Named
@ViewScoped
public class PolyStaffListMB implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Inject
    PolyStaffService polyStaffService;

    String opno;

    LazyDataModel<PolyStaff> patients;

    Filter<PolyStaff> filter = new Filter<>(new PolyStaff());

    List<PolyStaff> selectedPatients; //patients selected in checkbox column

    List<PolyStaff> filteredValue;// datatable filteredValue attribute (column filters)

    @PostConstruct
    public void initDataModel() {
        patients = new LazyDataModel<PolyStaff>() {
            @Override
            public List<PolyStaff> load(int first, int pageSize,
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
                List<PolyStaff> list = polyStaffService.paginate(filter);
                setRowCount((int) polyStaffService.count(filter));
                return list;
            }

            @Override
            public int getRowCount() {
                return super.getRowCount();
            }

            @Override
            public PolyStaff getRowData(String key) {
                return polyStaffService.findByOpno(key);
            }
        };
    }

    public void clear() {
        filter = new Filter<PolyStaff>(new PolyStaff());
    }

    public List<String> completeModelOpno(String query) {
        List<String> result = polyStaffService.getOpno(query);
        return result;
    }

    public List<String> completeModelPolytechnic(String query) {
        List<String> result = polyStaffService.getPolytechnic(query);
        return result;
    }
    
    public List<String> completeModel(String query) {
        List<String> result = polyStaffService.getNames(query);
        return result;
    }

  
    
    public void findPatientById(String opno) {
        if (opno == null) {
            throw new BusinessException("Provide PolyStaff Account Type ID to load");
        }
        selectedPatients.add(polyStaffService.findByOpno(opno));
    }

    public void delete() {
        int numPatients = 0;
        for (PolyStaff selectedCar : selectedPatients) {
            numPatients++;
            polyStaffService.remove(selectedCar);
        }
        selectedPatients.clear();
        addDetailMessage(numPatients + " PolyStaff Account Type deleted successfully!");
    }

    public List<PolyStaff> getFilteredValue() {
        return filteredValue;
    }

    public void setFilteredValue(List<PolyStaff> filteredValue) {
        this.filteredValue = filteredValue;
    }

    public LazyDataModel<PolyStaff> getPatients() {
        return patients;
    }

    public void setPatients(LazyDataModel<PolyStaff> patients) {
        this.patients = patients;
    }

    public Filter<PolyStaff> getFilter() {
        return filter;
    }

    public void setFilter(Filter<PolyStaff> filter) {
        this.filter = filter;
    }

    public List<PolyStaff> getSelectedPatients() {
        return selectedPatients;
    }

    public void setSelectedPatients(List<PolyStaff> selectedPatients) {
        this.selectedPatients = selectedPatients;
    }

    public String getOpno() {
        return opno;
    }

    public void setOpno(String opno) {
        this.opno = opno;
    }
}
