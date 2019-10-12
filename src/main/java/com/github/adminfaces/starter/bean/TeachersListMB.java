package com.github.adminfaces.starter.bean;

import com.github.adminfaces.starter.infra.model.Filter;
import com.github.adminfaces.starter.model.Teacher;
import com.github.adminfaces.starter.service.TeacherService;
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
public class TeachersListMB implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Inject
    TeacherService teacherService;

    String opno;

    LazyDataModel<Teacher> patients;

    Filter<Teacher> filter = new Filter<>(new Teacher());

    List<Teacher> selectedPatients; //patients selected in checkbox column

    List<Teacher> filteredValue;// datatable filteredValue attribute (column filters)

    @PostConstruct
    public void initDataModel() {
        patients = new LazyDataModel<Teacher>() {
            @Override
            public List<Teacher> load(int first, int pageSize,
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
                List<Teacher> list = teacherService.paginate(filter);
                setRowCount((int) teacherService.count(filter));
                return list;
            }

            @Override
            public int getRowCount() {
                return super.getRowCount();
            }

            @Override
            public Teacher getRowData(String key) {
                return teacherService.findByOpno(key);
            }
        };
    }

    public void clear() {
        filter = new Filter<Teacher>(new Teacher());
    }

    public List<String> completeModelOpno(String query) {
        List<String> result = teacherService.getOpno(query);
        return result;
    }

    public List<String> completeModelPhone(String query) {
        List<String> result = teacherService.getPhones(query);
        return result;
    }
    
    public List<String> completeModel(String query) {
        List<String> result = teacherService.getNames(query);
        return result;
    }

  
    
    public void findPatientById(String opno) {
        if (opno == null) {
            throw new BusinessException("Provide Teacher Account Type ID to load");
        }
        selectedPatients.add(teacherService.findByOpno(opno));
    }

    public void delete() {
        int numPatients = 0;
        for (Teacher selectedCar : selectedPatients) {
            numPatients++;
            teacherService.remove(selectedCar);
        }
        selectedPatients.clear();
        addDetailMessage(numPatients + " Teacher Account Type deleted successfully!");
    }

    public List<Teacher> getFilteredValue() {
        return filteredValue;
    }

    public void setFilteredValue(List<Teacher> filteredValue) {
        this.filteredValue = filteredValue;
    }

    public LazyDataModel<Teacher> getPatients() {
        return patients;
    }

    public void setPatients(LazyDataModel<Teacher> patients) {
        this.patients = patients;
    }

    public Filter<Teacher> getFilter() {
        return filter;
    }

    public void setFilter(Filter<Teacher> filter) {
        this.filter = filter;
    }

    public List<Teacher> getSelectedPatients() {
        return selectedPatients;
    }

    public void setSelectedPatients(List<Teacher> selectedPatients) {
        this.selectedPatients = selectedPatients;
    }

    public String getOpno() {
        return opno;
    }

    public void setOpno(String opno) {
        this.opno = opno;
    }
}
