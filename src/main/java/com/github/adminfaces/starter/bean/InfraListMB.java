package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.infra.model.Filter;
import com.github.adminfaces.starter.model.Infra;
import com.github.adminfaces.starter.service.InfraService;
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
import org.apache.log4j.Logger;

/**
 * Created by rmpestano on 12/02/17.
 */

@Named
@ViewScoped
public class InfraListMB implements Serializable {
private static Logger logger = Logger.getLogger(InfraListMB.class);
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Inject
    InfraService infraService;

    String opno;

    LazyDataModel<Infra> patients;

    Filter<Infra> filter = new Filter<>(new Infra());

    List<Infra> selectedPatients; //patients selected in checkbox column

    List<Infra> filteredValue;// datatable filteredValue attribute (column filters)

    @PostConstruct
    public void initDataModel() {
        patients = new LazyDataModel<Infra>() {
            @Override
            public List<Infra> load(int first, int pageSize,
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
                List<Infra> list = infraService.paginate(filter);
                setRowCount((int) infraService.count(filter));
                return list;
            }

            @Override
            public int getRowCount() {
                return super.getRowCount();
            }

            @Override
            public Infra getRowData(String key) {
                return infraService.findByOpno(key);
            }
        };
    }

    public void clear() {
        filter = new Filter<Infra>(new Infra());
    }

    public List<String> completeModelOpno(String query) {
        List<String> result = infraService.getOpno(query);
        return result;
    }

    public List<String> completeModel(String query) {
        List<String> result = infraService.getNames(query);
        return result;
    }

    public List<String> completeModelSponsor(String query) {
        List<String> result = infraService.getSponsors(query);
        return result;
    }
    
    
    public void findPatientById(String opno) {
        if (opno == null) {
            throw new BusinessException("Provide Infra Account Type ID to load");
        }
        selectedPatients.add(infraService.findByOpno(opno));
    }

    public void delete() {
        int numPatients = 0;
        for (Infra selectedCar : selectedPatients) {
            numPatients++;
            infraService.remove(selectedCar);
        }
        selectedPatients.clear();
        addDetailMessage(numPatients + " Infra Account Type deleted successfully!");
    }

    public List<Infra> getFilteredValue() {
        return filteredValue;
    }

    public void setFilteredValue(List<Infra> filteredValue) {
        this.filteredValue = filteredValue;
    }

    public LazyDataModel<Infra> getPatients() {
        return patients;
    }

    public void setPatients(LazyDataModel<Infra> patients) {
        this.patients = patients;
    }

    public Filter<Infra> getFilter() {
        return filter;
    }

    public void setFilter(Filter<Infra> filter) {
        this.filter = filter;
    }

    public List<Infra> getSelectedPatients() {
        return selectedPatients;
    }

    public void setSelectedPatients(List<Infra> selectedPatients) {
        this.selectedPatients = selectedPatients;
    }

    public String getOpno() {
        return opno;
    }

    public void setOpno(String opno) {
        this.opno = opno;
    }
}
