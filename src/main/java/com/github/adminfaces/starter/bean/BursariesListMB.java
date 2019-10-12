package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.infra.model.Filter;
import com.github.adminfaces.starter.model.Bursary;
import com.github.adminfaces.starter.service.BursaryService;
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
public class BursariesListMB implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Inject
    BursaryService bursaryService;

    String opno;

    LazyDataModel<Bursary> patients;

    Filter<Bursary> filter = new Filter<>(new Bursary());

    List<Bursary> selectedPatients; //patients selected in checkbox column

    List<Bursary> filteredValue;// datatable filteredValue attribute (column filters)

    @PostConstruct
    public void initDataModel() {
        patients = new LazyDataModel<Bursary>() {
            @Override
            public List<Bursary> load(int first, int pageSize,
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
                List<Bursary> list = bursaryService.paginate(filter);
                setRowCount((int) bursaryService.count(filter));
                return list;
            }

            @Override
            public int getRowCount() {
                return super.getRowCount();
            }

            @Override
            public Bursary getRowData(String key) {
                return bursaryService.findByOpno(key);
            }
        };
    }

    public void clear() {
        filter = new Filter<Bursary>(new Bursary());
    }

    public List<String> completeModelOpno(String query) {
        List<String> result = bursaryService.getOpno(query);
        return result;
    }

    public List<String> completeModel(String query) {
        List<String> result = bursaryService.getNames(query);
        return result;
    }

    
    public void findPatientById(String opno) {
        if (opno == null) {
            throw new BusinessException("Provide Bursary Account Type ID to load");
        }
        selectedPatients.add(bursaryService.findByOpno(opno));
    }

    public void delete() {
        int numPatients = 0;
        for (Bursary selectedCar : selectedPatients) {
            numPatients++;
            bursaryService.remove(selectedCar);
        }
        selectedPatients.clear();
        addDetailMessage(numPatients + " Bursary Account Type deleted successfully!");
    }

    public List<Bursary> getFilteredValue() {
        return filteredValue;
    }

    public void setFilteredValue(List<Bursary> filteredValue) {
        this.filteredValue = filteredValue;
    }

    public LazyDataModel<Bursary> getPatients() {
        return patients;
    }

    public void setPatients(LazyDataModel<Bursary> patients) {
        this.patients = patients;
    }

    public Filter<Bursary> getFilter() {
        return filter;
    }

    public void setFilter(Filter<Bursary> filter) {
        this.filter = filter;
    }

    public List<Bursary> getSelectedPatients() {
        return selectedPatients;
    }

    public void setSelectedPatients(List<Bursary> selectedPatients) {
        this.selectedPatients = selectedPatients;
    }

    public String getOpno() {
        return opno;
    }

    public void setOpno(String opno) {
        this.opno = opno;
    }
}
