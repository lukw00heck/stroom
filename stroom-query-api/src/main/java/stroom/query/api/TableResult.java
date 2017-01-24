/*
 * Copyright 2016 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package stroom.query.api;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import java.util.Arrays;

@JsonPropertyOrder({"componentId", "rows", "resultRange", "totalResults", "error"})
@XmlType(name = "tableResult", propOrder = {"rows", "resultRange", "totalResults", "error"})
public class TableResult extends Result {
    private static final long serialVersionUID = -2964122512841756795L;

    private Row[] rows;
    private OffsetRange resultRange;
    private Integer totalResults;
    private String error;

    public TableResult() {
    }

    public TableResult(final String componentId) {
        super(componentId);
    }

    @XmlElementWrapper(name = "rows")
    @XmlElement(name = "row")
    public Row[] getRows() {
        return rows;
    }

    public void setRows(final Row[] rows) {
        this.rows = rows;
    }

    @XmlElement
    public OffsetRange getResultRange() {
        return resultRange;
    }

    public void setResultRange(final OffsetRange resultRange) {
        this.resultRange = resultRange;
    }

    @XmlElement
    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(final Integer totalResults) {
        this.totalResults = totalResults;
    }

    @XmlElement
    public String getError() {
        return error;
    }

    public void setError(final String error) {
        this.error = error;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof TableResult)) return false;

        final TableResult that = (TableResult) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(rows, that.rows)) return false;
        if (resultRange != null ? !resultRange.equals(that.resultRange) : that.resultRange != null) return false;
        if (totalResults != null ? !totalResults.equals(that.totalResults) : that.totalResults != null) return false;
        return error != null ? error.equals(that.error) : that.error == null;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(rows);
        result = 31 * result + (resultRange != null ? resultRange.hashCode() : 0);
        result = 31 * result + (totalResults != null ? totalResults.hashCode() : 0);
        result = 31 * result + (error != null ? error.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        if (rows == null) {
            return "";
        }

        return rows.length + " rows";
    }
}
