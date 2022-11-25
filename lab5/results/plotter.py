from matplotlib import pyplot as plt

DATA = {
  '4-conditions': {
    'thread_numbers': [10, 20, 30, 40, 50, 60, 70, 80, 90, 100],
    'access_counts': [265517.00, 241000.50, 209118.20, 224709.10, 229408.10, 213602.00, 206919.50, 216338.50, 205357.30, 190850.10],
    'total_cpu_times': [909.34, 916.94, 873.45, 916.13, 923.49, 906.06, 915.37, 929.79, 924.63, 917.59],
    'avg_cpu_times': [90.93, 45.85, 29.12, 22.90, 18.47, 15.10, 13.08, 11.62, 10.27, 9.18],
    'access_per_cpu_second': [291989.75, 262830.75, 239415.76, 245280.88, 248414.01, 235747.0, 226049.55, 232675.56, 222096.01, 207989.54]
  },

  'nested-locks': {
    'thread_numbers': [10, 20, 30, 40, 50, 60, 70, 80, 90, 100],
    'access_counts': [380157.30, 377326.50, 380397.80, 369348.40, 347590.10, 335600.70, 326029.90, 336263.90, 330939.70, 328771.80],
    'total_cpu_times': [1430.31, 1449.19, 1486.79, 1473.30, 1473.53, 1499.56, 1447.94, 1487.49, 1489.96, 1527.05],
    'avg_cpu_times': [143.03, 72.46, 49.56, 36.83, 29.47, 24.99, 20.68, 18.59, 16.56, 15.27],
    'access_per_cpu_second': [265785.89, 260369.94, 255852.30, 250694.65, 235889.52, 223799.60, 225168.05, 226060.87, 222112.89, 215298.21]
  }
}


def draw_plot(X, Y, title, x_label, y_label):
  plt.plot(X, Y)
  plt.title(title)
  plt.xlabel(x_label)
  plt.ylabel(y_label)
  plt.show()

if __name__ == '__main__':
  data_source = 'nested-locks'

  data = DATA[data_source]
  X = data['thread_numbers']
  # draw_plot(X, data['access_counts'], "Number of operations", "threads", "operations")
  # draw_plot(X, data['total_cpu_times'], "Total CPU time", "threads", "CPU time")
  # draw_plot(X, data['avg_cpu_times'], "Average CPU time", "threads", "Average CPU time")
  draw_plot(X, data['access_per_cpu_second'], "Operations per CPU second", "threads", "operations / CPU second")
