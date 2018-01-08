export namespace public_ {
    export interface Person1 {
        firstName: string;
        id: number;
    }
}

export namespace public_ {
    export interface Checks {
        yesno: boolean;
    }
}

export namespace public_ {
    export interface Person2 {
        firstName: string;
        id: number;
        age: number;
    }
}

export namespace  {
    export interface LoadAllPersonsEscaped2 {
        theid: number;
    }
}

export namespace  {
    export interface LoadAllPersons {
        firstName: string;
        id: number;
    }
}

export namespace  {
    export interface LoadAllPersonsEscaped1 {
        theid: number;
    }
}

export namespace subdir {
    export interface LoadPersonsByAgeAndFirstName {
        firstName: string;
        id: number;
        age: number;
    }
}

