export namespace com_w11k_lsql_cli_tests_schema_public {
    export interface Person1Row {
        id: number;
        firstName?: string;
    }
}

export namespace com_w11k_lsql_cli_tests_schema_public {
    export interface Person1Map {
        id: number;
        first_name?: string;
    }
}

export namespace com_w11k_lsql_cli_tests_schema_public {
    export interface A_TableRow {
        id: number;
    }
}

export namespace com_w11k_lsql_cli_tests_schema_public {
    export interface A_TableMap {
        id: number;
    }
}

export namespace com_w11k_lsql_cli_tests_schema_public {
    export interface ChecksRow {
        yesno: boolean;
    }
}

export namespace com_w11k_lsql_cli_tests_schema_public {
    export interface ChecksMap {
        yesno: boolean;
    }
}

export namespace com_w11k_lsql_cli_tests_schema_public {
    export interface Person2Row {
        id: number;
        firstName?: string;
        age?: number;
    }
}

export namespace com_w11k_lsql_cli_tests_schema_public {
    export interface Person2Map {
        id: number;
        first_name?: string;
        age?: number;
    }
}

export namespace com_w11k_lsql_cli_tests_schema_public {
    export interface Custom_ConverterRow {
        field?: number;
    }
}

export namespace com_w11k_lsql_cli_tests_schema_public {
    export interface Custom_ConverterMap {
        field?: number;
    }
}

export namespace com_w11k_lsql_cli_tests_stmtswithcustomconverter {
    export interface LoadRow {
        field?: number;
    }
}

export namespace com_w11k_lsql_cli_tests_stmtswithcustomconverter {
    export interface LoadMap {
        field?: number;
    }
}

export namespace com_w11k_lsql_cli_tests_stmts1 {
    export interface LoadAllPersonsEscaped2Row {
        theid: number;
    }
}

export namespace com_w11k_lsql_cli_tests_stmts1 {
    export interface LoadAllPersonsEscaped2Map {
        theid: number;
    }
}

export namespace com_w11k_lsql_cli_tests_stmts1 {
    export interface LoadAllPersonsRow {
        id: number;
        firstName?: string;
    }
}

export namespace com_w11k_lsql_cli_tests_stmts1 {
    export interface LoadAllPersonsMap {
        id: number;
        first_name?: string;
    }
}

export namespace com_w11k_lsql_cli_tests_stmts1 {
    export interface LoadAllPersonsEscaped1Row {
        theid: number;
    }
}

export namespace com_w11k_lsql_cli_tests_stmts1 {
    export interface LoadAllPersonsEscaped1Map {
        theid: number;
    }
}

export namespace com_w11k_lsql_cli_tests_stmts1 {
    export interface KeepUnderscoreForCamelCaseRow {
        aField: number;
        afield?: string;
    }
}

export namespace com_w11k_lsql_cli_tests_stmts1 {
    export interface KeepUnderscoreForCamelCaseMap {
        a_field: number;
        afield?: string;
    }
}

export namespace com_w11k_lsql_cli_tests_stmts1 {
    export interface QueryParamsWithDotRow {
        id: number;
        firstName?: string;
    }
}

export namespace com_w11k_lsql_cli_tests_stmts1 {
    export interface QueryParamsWithDotMap {
        id: number;
        first_name?: string;
    }
}

export namespace com_w11k_lsql_cli_tests_subdir_subsubdir_stmtscamelcase2 {
    export interface LoadPersonsByAgeAndFirstNameRow {
        id: number;
        firstName?: string;
        age?: number;
    }
}

export namespace com_w11k_lsql_cli_tests_subdir_subsubdir_stmtscamelcase2 {
    export interface LoadPersonsByAgeAndFirstNameMap {
        id: number;
        first_name?: string;
        age?: number;
    }
}

export namespace com_w11k_lsql_cli_tests {
    export interface DummyDtoRow {
        fieldA?: string;
    }
}

export namespace com_w11k_lsql_cli_tests {
    export interface DummyDtoMap {
        fieldA?: string;
    }
}

export namespace com_w11k_lsql_cli_tests_sub_for_dto {
    export interface SubDummyDtoRow {
        fieldA?: string;
        fieldB?: number;
    }
}

export namespace com_w11k_lsql_cli_tests_sub_for_dto {
    export interface SubDummyDtoMap {
        fieldA?: string;
        fieldB?: number;
    }
}

