function F= saveFPFN()

n_iter = 300;

FP3 = zeros(1,n_iter); % Memory preallocation
for i=1:100     
   FP3(i) = FalsePositiveGBF(i,3,365);
end
for i=101:300
    m=(i-100)*100;     
   FP3(i) = FalsePositiveGBF(m,3,365);
end

FN3 = zeros(1,n_iter); % Memory preallocation
for i=1:100     
   FN3(i) = FalseNegativeGBF(i,3,365);
end
for i=101:300
    m=(i-100)*100;
    FN3(i) = FalseNegativeGBF(m,3,365);
end


FP5 = zeros(1,n_iter); % Memory preallocation
for i=1:100     
   FP5(i) = FalsePositiveGBF(i,5,565);
end
for i=101:300
    m=(i-100)*100;
    FP5(i) = FalsePositiveGBF(m,5,365);
end

FN5 = zeros(1,n_iter); % Memory preallocation
for i=1:100     
   FN5(i) = FalseNegativeGBF(i,5,365);
end
for i=101:300
    m=(i-100)*100;
    FN5(i) = FalseNegativeGBF(m,5,365);
end


FP10 = zeros(1,n_iter); % Memory preallocation
for i=1:100     
   FP10(i) = FalsePositiveGBF(i,10,365);
end
for i=101:300
    m=(i-100)*100;
    FP10(i) = FalsePositiveGBF(m,10,365);
end

FN10 = zeros(1,n_iter); % Memory preallocation
for i=1:100     
   FN10(i) = FalseNegativeGBF(i,10,365);
end
for i=101:300
    m=(i-100)*100;
    FN10(i) = FalseNegativeGBF(m,10,365);
end

m=[(1:100),(100:100:20000)];
F=[m',FP3', FN3',FP5',FN5',FP10',FN10'];