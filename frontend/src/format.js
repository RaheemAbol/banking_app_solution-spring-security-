export function money(value) {
  return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(value);
}
export function timestamp(value) {
  return new Date(value + 'Z').toLocaleString();
}
