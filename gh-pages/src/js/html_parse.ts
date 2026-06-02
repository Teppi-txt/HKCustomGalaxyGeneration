export type BinderParams<T> = {
  id: string;
  getGlobal?: () => T;
  setGlobal: (v: T) => void;
  isValid?: (v: T) => boolean;
  convert?: (v: any) => T;
};

export function bindInput<T>({ id, setGlobal, isValid, convert }: BinderParams<T>) {
  const form_value = document.getElementById(id);
  const form_validation = document.getElementById(id + "Validation");

  const onchangeHandler = (form_validation && isValid) ? (e: Event) => {
    // @ts-ignore
    const target: HTMLElement = e.target;
    // @ts-ignore
    const value: T = convert ? convert(target.value) : target.value;
    if (isValid(value)) {
      setGlobal(value);
      target.classList.remove("is-invalid");
      form_validation.classList.add("d-none");
    } else {
      target.classList.add("is-invalid");
      form_validation.classList.remove("d-none");
    }
  } : ((e: Event) => {
    // @ts-ignore
    const value: T = e.target.value;
    setGlobal(convert ? convert(value) : value);
  });

  form_value.onchange = onchangeHandler;
}

export function bindCheckboxInput({ id, setGlobal }: BinderParams<boolean>) {
  const form_value = document.getElementById(id);
  form_value.onchange = ((e: Event) => {
    // @ts-ignore
    setGlobal(e.target.checked);
  });
}

export function bindNumberInput({ id, setGlobal, isValid }: BinderParams<number>) {
  bindInput<number>({ id, setGlobal, isValid, convert: (v : any) => parseInt(v) });
}
