function FN= FalseNegativeGBF(m,k,n)
k0=k/2;
k1=k-k0;
q0= (1 - exp(-k0 ./ m));
q1= (1 - exp(-k1 ./ m)).* exp(-k0 ./ m); 

b0= m.*q0;
b1=m.*q1;

temp_f_i=0;
for i=0:n
	p00_n_i= (1-q0-q1) .^i  +  (q0 ./(q0+q1)) .* (1- ( (1-q0-q1) .^i) );
	p11_n_i= (1-q0-q1) .^i  +  (q1 ./(q0+q1)) .* (1- ( (1-q0-q1) .^i) );
	temp_f_i=temp_f_i + (1- p00_n_i .^ b0) .* (p11_n_i .^ b1);
end

FN=temp_f_i ./n;